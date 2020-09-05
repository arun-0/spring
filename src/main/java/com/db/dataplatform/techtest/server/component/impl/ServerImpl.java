package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import com.db.dataplatform.techtest.server.service.HadoopDataLakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataBodyService dataBodyServiceImpl;
    private final DataHeaderService dataHeaderServiceImpl;

    @Autowired
    private final HadoopDataLakeService hadoopDataLakeService;
    private final ModelMapper modelMapper;

    /**
     * @param envelope
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(DataEnvelope envelope, String checksum) throws NoSuchAlgorithmException, ExecutionException, InterruptedException {
        if(envelope == null || envelope.getDataHeader() == null || envelope.getDataBody() == null) {
            log.info("Empty Envelope or Block name.. not being persisted");
            return false;
        }

        // task2 - check MD5 checksum
        if(!getMd5Checksum(envelope).equals(checksum)) {
            log.info("Checking of MD5 checksum failed - invalid data Block {} not being persisted",
                    envelope.getDataHeader().getName());
            return false;
        }
        log.info("MD5 checksum succeeded for Block {}", envelope.getDataHeader().getName());

        // task1 - save to persistence
        persist(envelope);

        //task5 - Async call to the data lake service
        hadoopDataLakeService.pushIntoHadoopDataLake(envelope);

        return true;
    }

    @Override
    public List<DataEnvelope> getDataEnevelopeByBlocktype(BlockTypeEnum blocktype) {
        List<DataBodyEntity> dataBodyEntityList =  dataBodyServiceImpl.getDataByBlockType(blocktype);

        if (dataBodyEntityList == null) {
            log.info("No Data Envelope found for type {}", blocktype);
            return null;
        } else {
            log.info("{} Data Envelope found for type {}", dataBodyEntityList.size(), blocktype);
        }

        return dataBodyEntityList.stream()
                    .map(p -> {
                        DataBody dataBody = modelMapper.map(p, DataBody.class);
                        DataHeader dataHeader = modelMapper.map(p.getDataHeaderEntity(), DataHeader.class);
                        return DataEnvelope.builder().dataHeader(dataHeader).dataBody(dataBody).build();
                    })
                    .collect(Collectors.toList());

    }

    @Override
    public boolean updateBlockType(String blockName, BlockTypeEnum blockType) {
        boolean isBlockTypeUpdated = dataHeaderServiceImpl.updateBlockType(blockName, blockType);
        if(!isBlockTypeUpdated) {
            log.info("No Block found for name {}", blockName);
        } else {
            log.info("Successfully updated the Block {}", blockName);
        }
        return isBlockTypeUpdated;
    }

    private String getMd5Checksum(DataEnvelope envelope) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(envelope.getDataBody().getDataBody().getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest);
    }

    private void persist(DataEnvelope envelope) {
        log.info("Persisting the Block : {}", envelope.getDataHeader().getName());

        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);
        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);

        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
        log.info("Block persisted successfully : {}", envelope.getDataHeader().getName());
    }

}

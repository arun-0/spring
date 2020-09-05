package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataHeaderServiceImpl implements com.db.dataplatform.techtest.server.service.DataHeaderService {

    private final DataHeaderRepository dataHeaderRepository;

    @Override
    @Transactional
    public void saveHeader(DataHeaderEntity entity) {
        dataHeaderRepository.save(entity);
    }

    @Override
    @Transactional
    public boolean updateBlockType(String blockName, BlockTypeEnum blockType) {
        DataHeaderEntity dataHeaderEntity = dataHeaderRepository.findByName(blockName).orElse(null);
        if(dataHeaderEntity == null) {
            return false;
        }
        dataHeaderEntity.setBlocktype(blockType);
        dataHeaderRepository.save(dataHeaderEntity);
        return true;
    }

}

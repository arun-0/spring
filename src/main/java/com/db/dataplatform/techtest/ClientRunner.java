package com.db.dataplatform.techtest;

import com.db.dataplatform.techtest.client.api.model.BlockTypeEnum;
import com.db.dataplatform.techtest.client.api.model.DataBody;
import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.api.model.DataHeader;
import com.db.dataplatform.techtest.client.component.Client;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class ClientRunner {

    public static final String HEADER_NAME = "TSLA-USDGBP-10Y";
    public static final String DUMMY_DATA = "AKCp5fU4WNWKBVvhXsbNhqk33tawri9iJUkA5o4A6YqpwvAoYjajVw8xdEw6r9796h1wEp29D";
    public static final String DUMMY_DATA_CHECKSUM = "CECFD3953783DF706878AAEC2C22AA70";

    @Autowired
    private Client client;

    @EventListener(ApplicationReadyEvent.class)
    public void initiatePushDataFlow() throws JsonProcessingException, UnsupportedEncodingException {
        pushData();
        queryData();
        updateData();
        queryData();

        // for Negative tests
//        pushDataWithEmptyName();
//        pushDataWithEmptyBody();
//        queryDataInvalidBlockType();
//        updateDataInValidBlockType();
//        updateDataInValidBlockName();
//        pushAnotherData();

    }

    private void pushData() throws JsonProcessingException {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(HEADER_NAME, BlockTypeEnum.BLOCKTYPEA);
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

        client.pushData(dataEnvelope, DUMMY_DATA_CHECKSUM);
    }

    private void queryData() {
        client.getData(BlockTypeEnum.BLOCKTYPEA.name());
        client.getData(BlockTypeEnum.BLOCKTYPEB.name());
    }

    private void updateData() throws UnsupportedEncodingException {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(HEADER_NAME, BlockTypeEnum.BLOCKTYPEA);
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

        client.updateData(HEADER_NAME, BlockTypeEnum.BLOCKTYPEB.name(), dataEnvelope);
    }

    private void pushDataWithEmptyName() throws JsonProcessingException {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader("", BlockTypeEnum.BLOCKTYPEA);
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

        client.pushData(dataEnvelope, DUMMY_DATA_CHECKSUM);
    }

    private void pushDataWithEmptyBody() throws JsonProcessingException {
        DataBody dataBody = new DataBody("");
        DataHeader dataHeader = new DataHeader(HEADER_NAME, BlockTypeEnum.BLOCKTYPEA);
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

        client.pushData(dataEnvelope, "D41D8CD98F00B204E9800998ECF8427E");
    }

    private void queryDataInvalidBlockType() {
        client.getData("XYZ");
    }

    private void updateDataInValidBlockType() throws UnsupportedEncodingException {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(HEADER_NAME, BlockTypeEnum.BLOCKTYPEA);
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

        client.updateData(HEADER_NAME, "XYZ", dataEnvelope);
    }

    private void updateDataInValidBlockName() throws UnsupportedEncodingException {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(HEADER_NAME, BlockTypeEnum.BLOCKTYPEA);
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

         client.updateData(" ", BlockTypeEnum.BLOCKTYPEB.name(), dataEnvelope);

    }

    private void pushAnotherData() throws JsonProcessingException {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(HEADER_NAME + "_2", BlockTypeEnum.BLOCKTYPEB);
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

        client.pushData(dataEnvelope, DUMMY_DATA_CHECKSUM);
    }

}

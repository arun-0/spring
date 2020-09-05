package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface Server {
    boolean saveDataEnvelope(DataEnvelope envelope, String checksum) throws IOException, NoSuchAlgorithmException, ExecutionException, InterruptedException;
    List<DataEnvelope> getDataEnevelopeByBlocktype(BlockTypeEnum blocktype);
    boolean updateBlockType(String blockName, BlockTypeEnum blockType);
}

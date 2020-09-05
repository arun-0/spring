package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

import javax.transaction.Transactional;

public interface DataHeaderService {

    @Transactional
    void saveHeader(DataHeaderEntity entity);

    @Transactional
    boolean updateBlockType(String blockName, BlockTypeEnum blockType);
}

package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;

import javax.transaction.Transactional;
import java.util.List;

public interface DataBodyService {
    @Transactional
    void saveDataBody(DataBodyEntity dataBody);

    @Transactional
    List<DataBodyEntity> getDataByBlockType(BlockTypeEnum blockType);

}

package com.db.dataplatform.techtest.api.model;

import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import org.junit.jupiter.api.Test;

import static com.db.dataplatform.techtest.TestDataHelper.TEST_NAME;
import static org.assertj.core.api.Assertions.assertThat;

public class DataHeaderTests {

    @Test
    public void assignDataHeaderFieldsShouldWorkAsExpected() {
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA);

        assertThat(dataHeader.getName()).isEqualTo(TEST_NAME);
        assertThat(dataHeader.getBlockType()).isEqualTo(BlockTypeEnum.BLOCKTYPEA);
    }
}

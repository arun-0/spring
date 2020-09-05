package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataBodyEntity;
import static com.db.dataplatform.techtest.TestDataHelper.createTestDataHeaderEntity;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DataStoreRepositoryTest {

    @Autowired
    DataStoreRepository dataStoreRepository;

    private DataBodyEntity expectedDataBodyEntity;

    @BeforeEach
    public void setup() {
        expectedDataBodyEntity = createTestDataBodyEntity(createTestDataHeaderEntity(Instant.now()));
        dataStoreRepository.save(expectedDataBodyEntity);
    }

    @Test
    public void testSaveDataEnvelope() {
        assertThat(dataStoreRepository.findById(expectedDataBodyEntity.getDataStoreId()).get()).isEqualTo(expectedDataBodyEntity);
    }

    @Test
    public void testFindByBlocktype() {
        List<DataBodyEntity> dataBodyEntity = dataStoreRepository
                .findByBlocktype(expectedDataBodyEntity.getDataHeaderEntity().getBlocktype())
                .get();

        assertThat(dataBodyEntity.size()).isEqualTo(1);
        assertThat(dataBodyEntity.get(0)).isEqualTo(expectedDataBodyEntity);
    }

    @Test
    public void testFindByBlockname() {
        DataBodyEntity dataBodyEntity = dataStoreRepository
                .findByBlockname(expectedDataBodyEntity.getDataHeaderEntity().getName())
                .get();

        assertThat(dataBodyEntity).isEqualTo(expectedDataBodyEntity);
    }

    @Test
    public void testUpdateBlockType() {
        expectedDataBodyEntity.getDataHeaderEntity().setBlocktype(BlockTypeEnum.BLOCKTYPEB);
        dataStoreRepository.save(expectedDataBodyEntity);

        assertThat(dataStoreRepository.findById(expectedDataBodyEntity.getDataStoreId()).get()).isEqualTo(expectedDataBodyEntity);
    }

}
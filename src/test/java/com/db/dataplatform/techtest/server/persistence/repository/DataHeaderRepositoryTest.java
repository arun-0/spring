package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataHeaderEntity;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DataHeaderRepositoryTest {

    @Autowired
    DataHeaderRepository dataStoreRepository;

    @Test
    public void testSave() {
        DataHeaderEntity testDataHeaderEntity = createTestDataHeaderEntity(Instant.now());
        dataStoreRepository.save(testDataHeaderEntity);

        DataHeaderEntity dataHeaderEntity = dataStoreRepository.findById(testDataHeaderEntity.getDataHeaderId()).orElse(null);
        assertThat(dataHeaderEntity).isEqualTo(testDataHeaderEntity);
    }

    @Test
    public void testFindByBlockName() {
        DataHeaderEntity testDataHeaderEntity = createTestDataHeaderEntity(Instant.now());
        dataStoreRepository.save(testDataHeaderEntity);
        DataHeaderEntity byBlockname = dataStoreRepository.findByName(testDataHeaderEntity.getName()).orElse(null);
        assertThat(byBlockname).isEqualTo(testDataHeaderEntity);
    }

    @Test
    public void testUpdateBlockType() {
        DataHeaderEntity testDataHeaderEntity = createTestDataHeaderEntity(Instant.now());
        dataStoreRepository.save(testDataHeaderEntity);

        DataHeaderEntity dataHeaderEntity = dataStoreRepository.findById(testDataHeaderEntity.getDataHeaderId()).orElse(null);
        assertThat(dataHeaderEntity).isEqualTo(testDataHeaderEntity);

        testDataHeaderEntity.setBlocktype(BlockTypeEnum.BLOCKTYPEB);
        dataStoreRepository.save(testDataHeaderEntity);

        dataHeaderEntity = dataStoreRepository.findById(testDataHeaderEntity.getDataHeaderId()).orElse(null);
        assertThat(dataHeaderEntity).isEqualTo(testDataHeaderEntity);

    }

}
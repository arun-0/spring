package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import com.db.dataplatform.techtest.server.service.impl.DataHeaderServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataHeaderEntity;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataHeaderServiceTests {

    @Mock
    private DataHeaderRepository dataHeaderRepositoryMock;

    private DataHeaderService dataHeaderService;
    private DataHeaderEntity expectedDataHeaderEntity;

    @BeforeEach
    public void setup() {
        expectedDataHeaderEntity = createTestDataHeaderEntity(Instant.now());
        dataHeaderService = new DataHeaderServiceImpl(dataHeaderRepositoryMock);
    }

    @Test
    public void shouldSaveDataHeaderEntityAsExpected(){
        when(dataHeaderRepositoryMock.save(expectedDataHeaderEntity))
                .thenReturn(expectedDataHeaderEntity);

        dataHeaderService.saveHeader(expectedDataHeaderEntity);

        verify(dataHeaderRepositoryMock, times(1))
                .save(eq(expectedDataHeaderEntity));
    }

    @Test
    public void shouldUpdateDataHeaderEntityAsExpected(){
        when(dataHeaderRepositoryMock.findByName(expectedDataHeaderEntity.getName()))
                .thenReturn(Optional.of(expectedDataHeaderEntity));

        boolean result = dataHeaderService.updateBlockType(expectedDataHeaderEntity.getName(), BlockTypeEnum.BLOCKTYPEB);
        Assertions.assertThat(result).isTrue();

        verify(dataHeaderRepositoryMock, times(1))
                .findByName(eq(expectedDataHeaderEntity.getName()));

        verify(dataHeaderRepositoryMock, times(1))
                .save(eq(expectedDataHeaderEntity));

    }

}

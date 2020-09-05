package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import com.db.dataplatform.techtest.server.service.HadoopDataLakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import static com.db.dataplatform.techtest.TestDataHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;
    @Mock
    private DataHeaderService dataHeaderServiceImplMock;
    @Mock
    private HadoopDataLakeService hadoopDataLakeService;

    private ModelMapper modelMapper;

    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope testDataEnvelope;

    private Server server;

    @BeforeEach
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();

        testDataEnvelope = createTestDataEnvelopeApiObject();
        expectedDataBodyEntity = modelMapper.map(testDataEnvelope.getDataBody(), DataBodyEntity.class);
        expectedDataBodyEntity.setDataHeaderEntity(modelMapper.map(testDataEnvelope.getDataHeader(), DataHeaderEntity.class));

        server = new ServerImpl(dataBodyServiceImplMock, dataHeaderServiceImplMock, hadoopDataLakeService, modelMapper);
    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException, ExecutionException, InterruptedException {
        assertThat(server.saveDataEnvelope(testDataEnvelope, getMd5Checksum(DUMMY_DATA))).isTrue();
        verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldNotSaveDataEnvelopeWhenIncorrectChecksumProvided() throws NoSuchAlgorithmException, IOException, ExecutionException, InterruptedException {
        assertThat(server.saveDataEnvelope(testDataEnvelope, "wrong-checksum")).isFalse();
        verify(dataBodyServiceImplMock, never()).saveDataBody(any(DataBodyEntity.class));
    }


}

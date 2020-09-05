package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.service.HadoopDataLakeService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HadoopDataLakeServiceImplTest {

    private final static String URI_DATA_LAKE = "http://localhost:8090/hadoopserver/pushbigdata";

    @Mock
    private Server serverMock;

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    HadoopDataLakeService hadoopDataLakeService = new HadoopDataLakeServiceImpl();

    private final DataEnvelope testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();

    @Test
    public void testDataLakePushIsAsync() throws ExecutionException, InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType((MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(testDataEnvelope.getDataBody().getDataBody(), headers);

        when(restTemplate.exchange(URI_DATA_LAKE, HttpMethod.POST, entity, HttpStatus.class))
                .thenThrow(new RestClientException("Could Not Connect"));


        // this should not throw the exception - proving that it is an Async call
        CompletableFuture<Boolean> asyncResult = hadoopDataLakeService.pushIntoHadoopDataLake(testDataEnvelope);

        Awaitility.await().until(() -> asyncResult.isDone());

        // Only, trying accessing the result should tell the error
        assertThatExceptionOfType(ExecutionException.class)
                .isThrownBy(() -> asyncResult.get())
                .withMessage("org.springframework.web.client.RestClientException: Could Not Connect");

    }

    @Test
    public void pushIntoHadoopDataLakeShouldReturnTrueWhenRestCallSucceeds() throws ExecutionException, InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType((MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(testDataEnvelope.getDataBody().getDataBody(), headers);

        when(restTemplate.exchange(URI_DATA_LAKE, HttpMethod.POST, entity, HttpStatus.class)).thenReturn(new ResponseEntity(HttpStatus.OK));

        CompletableFuture<Boolean> asyncResult = hadoopDataLakeService.pushIntoHadoopDataLake(testDataEnvelope);
        assertThat(asyncResult.get()).isTrue();
    }

    @Test
    public void pushIntoHadoopDataLakeShouldReturnFalseWhenRestCallFails() throws ExecutionException, InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType((MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(testDataEnvelope.getDataBody().getDataBody(), headers);

        when(restTemplate.exchange(URI_DATA_LAKE, HttpMethod.POST, entity, HttpStatus.class))
                .thenReturn(new ResponseEntity(HttpStatus.BAD_REQUEST));

        CompletableFuture<Boolean> asyncResult = hadoopDataLakeService.pushIntoHadoopDataLake(testDataEnvelope);
        assertThat(asyncResult.get()).isFalse();
    }

}
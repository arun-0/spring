package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.service.HadoopDataLakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class HadoopDataLakeServiceImpl implements HadoopDataLakeService {

    @Autowired
    RestTemplate restTemplate;

    private final static String URI_DATA_LAKE = "http://localhost:8090/hadoopserver/pushbigdata";

    @Async
    @Override
    public CompletableFuture<Boolean> pushIntoHadoopDataLake(final DataEnvelope payload) throws ExecutionException, InterruptedException {
        return CompletableFuture.supplyAsync(() -> push(payload));
    }

    private boolean push(DataEnvelope payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType((MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(payload.getDataBody().getDataBody(), headers);

        ResponseEntity<HttpStatus> response = restTemplate.exchange(
                URI_DATA_LAKE, HttpMethod.POST, entity, HttpStatus.class);

        log.info("Data Lake push for Blockname \"{}\", status = {}",
                payload.getDataHeader().getName(), response.getStatusCode());

        return response.getStatusCode().is2xxSuccessful();
    }

}

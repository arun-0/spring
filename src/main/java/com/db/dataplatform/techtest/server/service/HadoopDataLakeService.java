package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface HadoopDataLakeService {
    CompletableFuture<Boolean> pushIntoHadoopDataLake(DataEnvelope envelope)
            throws ExecutionException, InterruptedException;
}

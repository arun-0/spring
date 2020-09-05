package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

    public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
    public static final String URI_PATCHDATA = "http://localhost:8090/dataserver/update/{name}/{newBlockType}";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void pushData(DataEnvelope dataEnvelope, String md5Checksum) {
        String uriString = UriComponentsBuilder
                .fromHttpUrl(URI_PUSHDATA)
                .queryParam("checksum", md5Checksum)
                .toUriString();

        log.info("\n");
        log.info("Pushing Block {} to Server \n(URL = {})", dataEnvelope.getDataHeader().getName(), uriString);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType((MediaType.APPLICATION_JSON));
        HttpEntity<DataEnvelope> entity = new HttpEntity<>(dataEnvelope, headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(uriString, HttpMethod.POST, entity, Boolean.class);

        Boolean booleanResponse = response.getBody();
        log.info("Server Result for Pushing Block {} : {}\n", dataEnvelope.getDataHeader().getName(), booleanResponse);

    }

    @Override
    public List<DataEnvelope> getData(String blockType) {
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("blockType", blockType);

        URI uri = URI_GETDATA.expand(urlParams);
        log.info("Querying Server for Data Envelopes for block type {} \n(URL = {})", blockType, uri);

        ResponseEntity<List<DataEnvelope>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<DataEnvelope>>() {});

        List<DataEnvelope> list = response.getBody();
        if(list == null) {
            log.info("Received No Data block for block type {} from Server\n", blockType);
            return null;
        } else {
            log.info("Received {} Data block for block type {} from Server as below:-\n\t\t{}\n", list.size(), blockType,
                    list.stream().map(e -> e.toString()).reduce("", String::concat));
        }

        return list;

    }

    @Override
    public boolean updateData(String blockName, String newBlockType, DataEnvelope dataEnvelope) {
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("name", blockName);
        urlParams.put("newBlockType", newBlockType);

        String patchUrl = UriComponentsBuilder
                .fromHttpUrl(URI_PATCHDATA)
                .uriVariables(urlParams)
                .toUriString();

        log.info("Updating Block {} to Server \n(URL = {})", dataEnvelope.getDataHeader().getName(), patchUrl);

        ResponseEntity<Boolean> response = restTemplate.exchange(patchUrl, HttpMethod.PATCH, null, Boolean.class);
        boolean booleanResponse = response.getBody();

//        boolean booleanResponse = restTemplate.patchForObject(patchUrl, null, Boolean.class);

        log.info("Update result from Server for new Block type {} for the Block-{} : {}\n", newBlockType, blockName, booleanResponse);
        return booleanResponse;
    }


}

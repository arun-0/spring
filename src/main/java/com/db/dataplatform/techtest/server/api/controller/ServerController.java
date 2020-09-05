package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
public class ServerController {

    private final Server server;

    //task1,2,5
    @PostMapping(value = "/pushdata")
    @ResponseBody
    public Boolean pushData(@Valid @RequestBody DataEnvelope dataEnvelope, @RequestParam String checksum) throws IOException, NoSuchAlgorithmException, ExecutionException, InterruptedException {
        log.info("Data Block received : {}", dataEnvelope.getDataHeader().getName());
        boolean checksumPass = server.saveDataEnvelope(dataEnvelope, checksum);
        return checksumPass;
    }

    //task3
    @GetMapping(value = "/data/{blockType}")
    @ResponseBody
    public List<DataEnvelope> getDataForBlocktype(@PathVariable BlockTypeEnum blockType) {
        log.info("Data envelope Request received for the block type: {}", blockType);
        return server.getDataEnevelopeByBlocktype(blockType);
    }

    //task4
    @PatchMapping(value = "/update/{name}/{newBlockType}")
    @ResponseBody
    public Boolean update(@PathVariable String name, @PathVariable BlockTypeEnum newBlockType) {
        log.info("Received request to update Blocktype for Block {} to type {}", name, newBlockType);
        return server.updateBlockType(name, newBlockType);
    }


}

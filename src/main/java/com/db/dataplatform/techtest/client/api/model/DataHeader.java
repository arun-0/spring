package com.db.dataplatform.techtest.client.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.validation.constraints.NotNull;

@JsonSerialize(as = DataHeader.class)
@JsonDeserialize(as = DataHeader.class)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class DataHeader {

    @NotNull
    public String name;

    @NotNull
    private BlockTypeEnum blockType;

}

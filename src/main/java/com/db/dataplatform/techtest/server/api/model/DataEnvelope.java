package com.db.dataplatform.techtest.server.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonSerialize(as = DataEnvelope.class)
@JsonDeserialize(as = DataEnvelope.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DataEnvelope {

    @NotNull
    @Valid
    private DataHeader dataHeader;

    @NotNull
    private DataBody dataBody;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataEnvelope that = (DataEnvelope) o;
        return dataHeader.equals(that.dataHeader) &&
                dataBody.equals(that.dataBody);
    }

}

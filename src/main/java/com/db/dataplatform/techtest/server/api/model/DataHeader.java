package com.db.dataplatform.techtest.server.api.model;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import static com.db.dataplatform.techtest.server.Constants.BLANK_BLOCK_NAME;

@JsonSerialize(as = DataHeader.class)
@JsonDeserialize(as = DataHeader.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DataHeader {

    @NotBlank(message = BLANK_BLOCK_NAME)
    private String name;

    private BlockTypeEnum blockType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataHeader that = (DataHeader) o;
        return name.equals(that.name) &&
                blockType == that.blockType;
    }

}

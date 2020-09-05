package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataStoreRepository extends JpaRepository<DataBodyEntity, Long> {

    //task3
    @Query("select body from DataBodyEntity body inner join body.dataHeaderEntity header where header.blocktype = :blocktype")
    public Optional<List<DataBodyEntity>> findByBlocktype(@Param("blocktype") BlockTypeEnum blocktype);

    //task4
    @Query("select body from DataBodyEntity body inner join body.dataHeaderEntity header where header.name = :blockname")
    public Optional<DataBodyEntity> findByBlockname(@Param("blockname") String blockname);

}

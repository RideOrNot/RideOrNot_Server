package com.example.hanium2023.repository;

import com.example.hanium2023.domain.entity.StationExit;
import com.example.hanium2023.domain.entity.StationExitID;
import com.example.hanium2023.domain.entity.StationExitTmp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationExitRepository extends JpaRepository<StationExit, StationExitID> {
    List<StationExit> findAll();
}

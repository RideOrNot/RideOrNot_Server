package com.example.hanium2023.repository;

import com.example.hanium2023.domain.entity.ExitUserCountPerTime;
import com.example.hanium2023.domain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExitUserCountPerTimeRepository extends JpaRepository<ExitUserCountPerTime, Long> {
    List<ExitUserCountPerTime> findAllByStationAndDatetimeStartsWith(Station station, String datetime);
}

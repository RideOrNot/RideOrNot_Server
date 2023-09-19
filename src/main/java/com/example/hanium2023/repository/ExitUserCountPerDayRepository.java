package com.example.hanium2023.repository;

import com.example.hanium2023.domain.entity.ExitUserCountPerDay;
import com.example.hanium2023.domain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExitUserCountPerDayRepository extends JpaRepository<ExitUserCountPerDay, Long> {
    List<ExitUserCountPerDay> findAllByStationAndDow(Station station, String dow);
}

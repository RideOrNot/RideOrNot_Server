package com.example.hanium2023.repository;

import com.example.hanium2023.domain.entity.Line;
import com.example.hanium2023.domain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Integer> {
    List<Station> findAllByStatnNameAndLine(String stationName, Line line);
    Optional<Station> findByStatnNameAndSKStationCodeIsNotNull(String stationName);
}

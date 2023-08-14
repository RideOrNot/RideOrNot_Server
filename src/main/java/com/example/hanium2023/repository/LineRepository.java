package com.example.hanium2023.repository;

import com.example.hanium2023.domain.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LineRepository extends JpaRepository<Line, Integer> {
    Optional<Line> findByCsvLine(Integer csvLine);
    Optional<Line> findByLineNameContains(String line);
}

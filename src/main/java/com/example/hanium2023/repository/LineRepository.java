package com.example.hanium2023.repository;

import com.example.hanium2023.domain.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Integer> {
    Optional<Line> findByCsvLine(Integer csvLine);
}

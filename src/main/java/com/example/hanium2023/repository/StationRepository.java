package com.example.hanium2023.repository;

import com.example.hanium2023.domain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {
}
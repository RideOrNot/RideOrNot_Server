package com.example.hanium2023.repository;

import com.example.hanium2023.domain.entity.BoardingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardingHistoryRepository extends JpaRepository<BoardingHistory, Long> {
}

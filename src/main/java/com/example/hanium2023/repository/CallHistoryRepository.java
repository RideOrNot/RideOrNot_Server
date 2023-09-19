package com.example.hanium2023.repository;

import com.example.hanium2023.domain.entity.BookMark;
import com.example.hanium2023.domain.entity.CallHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallHistoryRepository extends JpaRepository<CallHistory, Integer> {
}

package com.example.hanium2023.repository;

import com.example.hanium2023.domain.entity.BookMark;
import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.domain.entity.StationExit;
import com.example.hanium2023.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookMarkRepository extends JpaRepository<BookMark, Long> {

    //List<BookMark> findAll();
    Optional<BookMark> findByUserAndStation(User user, Station station);
}

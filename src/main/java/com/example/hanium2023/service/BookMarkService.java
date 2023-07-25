package com.example.hanium2023.service;

import com.example.hanium2023.domain.dto.bookmark.BookMarkRequest;
import com.example.hanium2023.domain.entity.BookMark;
import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.domain.entity.User;
import com.example.hanium2023.repository.BookMarkRepository;
import com.example.hanium2023.repository.StationRepository;
import com.example.hanium2023.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BookMarkService {
    private final BookMarkRepository bookMarkRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;


    /* public String bookMarkInfo(String stationName, String userId){
        return BookMark.
    }*/

    @Transactional // 북마크 추가
    // requestparam 어노테이션 사용
    public void insert(Long userId, Integer stationId) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        //if (bookMarkRepository.findByUserAndStation(user, station).isPresent()){
            //이미 북마크 되어있으면 북마크 취소
        //}

        BookMark bookMark = BookMark.builder()
                .station(station)
                .user(user)
                .build();

        bookMarkRepository.save(bookMark);
    }

    // requestbody 어노테이션 사용 시
    /* public void insert(BookMarkRequest bookMarkRequest) throws Exception {

        User user = userRepository.findById(bookMarkRequest.getUserId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        Station station = stationRepository.findById(bookMarkRequest.getStationId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        //if (bookMarkRepository.findByUserAndStation(user, station).isPresent()){
            //이미 북마크 되어있으면 북마크 취소
        //}

        BookMark bookMark = BookMark.builder()
                .station(station)
                .user(user)
                .build();

        bookMarkRepository.save(bookMark);
    }*/

    @Transactional // 북마크 삭제
    public void delete(Long userId, Integer stationId) throws ChangeSetPersister.NotFoundException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        Station station= stationRepository.findById(stationId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        BookMark bookMark = bookMarkRepository.findByUserAndStation(user, station)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        bookMarkRepository.delete(bookMark);
    }

    /*public void delete(BookMarkRequest bookMarkRequest) throws ChangeSetPersister.NotFoundException {

        User user = userRepository.findById(bookMarkRequest.getUserId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        Station station= stationRepository.findById(bookMarkRequest.getStationId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        BookMark bookMark = bookMarkRepository.findByUserAndStation(user, station)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        bookMarkRepository.delete(bookMark);
    }*/

}

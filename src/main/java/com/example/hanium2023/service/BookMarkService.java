package com.example.hanium2023.service;

import com.example.hanium2023.domain.entity.BookMark;
import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.domain.entity.User;
import com.example.hanium2023.repository.BookMarkRepository;
import com.example.hanium2023.repository.StationRepository;
import com.example.hanium2023.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookMarkService {
    private final BookMarkRepository bookMarkRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;


    // 북마크 추가
    public void insert(Long userId, Integer stationId) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        //if (bookMarkRepository.findByUserAndStation(user, station).isPresent()){
            //이미 북마크 되어있으면 북마크 취소 - 추가, 삭제 따로? or 이미 되어있으면 삭제?
        //}

        BookMark bookMark = BookMark.builder()
                .station(station)
                .user(user)
                .build();

        bookMarkRepository.save(bookMark);
    }


    // 북마크 삭제
    public void delete(Long userId, Integer stationId) throws ChangeSetPersister.NotFoundException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        Station station= stationRepository.findById(stationId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        BookMark bookMark = bookMarkRepository.findByUserAndStation(user, station)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        bookMarkRepository.delete(bookMark);
    }


    //북마크 조회
    public Long getBookMarkList(Long userId) {
        return userId; //return new StationInfoPageResponse(publicApiService.getRealTimeInfoForStationInfoPage(stationName, lineId), 0);
    }

}





    //requestbody 어노테이션 사용 시
    //북마크 추가
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


    //북마크 해제
    /*public void delete(BookMarkRequest bookMarkRequest) throws ChangeSetPersister.NotFoundException {

        User user = userRepository.findById(bookMarkRequest.getUserId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        Station station= stationRepository.findById(bookMarkRequest.getStationId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        BookMark bookMark = bookMarkRepository.findByUserAndStation(user, station)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        bookMarkRepository.delete(bookMark);
    }*/

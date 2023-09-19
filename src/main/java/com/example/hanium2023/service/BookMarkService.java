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

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookMarkService {
    private final BookMarkRepository bookMarkRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;

    public List<BookMark> getAllBookmarks() {
        return bookMarkRepository.findAll();
    }

    // 북마크 추가
    public void bookmarks(Long userId, Integer stationId) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        Station station= stationRepository.findById(stationId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        //북마크 해제
        if(false) {
            BookMark bookMark = bookMarkRepository.findByUserAndStation(user, station)
                    .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

            bookMarkRepository.delete(bookMark);

        }else{ //북마크 추가
            BookMark bookMark = BookMark.builder()
                    .station(station)
                    .user(user)
                    .build();

            bookMarkRepository.save(bookMark);
        }
    }
}
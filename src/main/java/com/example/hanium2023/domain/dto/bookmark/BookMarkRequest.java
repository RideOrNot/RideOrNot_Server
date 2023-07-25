package com.example.hanium2023.domain.dto.bookmark;

import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.domain.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookMarkRequest { // 북마크를 누른 유저와 역 ID를 받는다

    private Long userId;
    private Integer stationId;

    public BookMarkRequest(Long userId, Integer stationId) {
        this.userId = userId;
        this.stationId = stationId;
    }

}
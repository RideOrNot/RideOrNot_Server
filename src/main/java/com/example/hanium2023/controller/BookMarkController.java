package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.bookmark.BookMarkRequest;
import com.example.hanium2023.domain.dto.ressponse.Response;
import com.example.hanium2023.domain.entity.Station;
import com.example.hanium2023.domain.entity.User;
import com.example.hanium2023.service.BookMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.hanium2023.domain.dto.ressponse.Response.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookMarkController {
    private final BookMarkService bookMarkService;
    // 북마크 추가하면 추가한 아이디와 역 정보 전달? //stations, userId

    /*@PostMapping
    public String postBookMark(@RequestParam String stationName, @RequestParam String userId){
        return bookMarkService.bookMarkInfo(stationName, userId);
    }*/

    @PostMapping("/insert") //북마크 추가
    //RequestBody 어노테이션 -> @RequestParam 어노테이션 사용해야함?
    public Response<?> insert(@RequestParam Long userId, @RequestParam Integer stationId) throws Exception {
        bookMarkService.insert(userId, stationId);
        return success(null);
    } // requestparm 어노테이션 사용

    /*public Response<?> insert(@RequestBody @Valid BookMarkRequest bookMarkRequest) throws Exception {
        System.out.println(bookMarkRequest.getUserId());
        bookMarkService.insert(bookMarkRequest);
        return success(null);
    }*/ // requesrbody 어노테이션 사용

    @DeleteMapping("/delete") //북마크 해제
    public Response<?> delete(@RequestParam Long userId, @RequestParam Integer stationId) throws ChangeSetPersister.NotFoundException {
        bookMarkService.delete(userId, stationId);
        return success(null);
    }

    /*public Response<?> delete(@RequestBody @Valid BookMarkRequest bookMarkRequest) throws ChangeSetPersister.NotFoundException {
        bookMarkService.delete(bookMarkRequest);
        return success(null);
    }*/

    // 북마크 해제하면 해제한 아이디와 역 정보 전달?
    // 북마크 되어있는 역 정보들 불러오기
}
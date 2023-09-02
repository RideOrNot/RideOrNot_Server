package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.ressponse.Response;
import com.example.hanium2023.service.BookMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.hanium2023.domain.dto.ressponse.Response.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stations")
public class BookMarkController {
    private final BookMarkService bookMarkService;

    @PostMapping("/{stationId}/bookmarks") //북마크 추가, 삭제
    public Response<?> bookmarks(@RequestParam Long userId, @RequestParam Integer stationId) throws Exception {
        bookMarkService.bookmarks(userId, stationId);
        return success(null);
    }
}
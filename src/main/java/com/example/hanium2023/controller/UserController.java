package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.ressponse.Response;
import com.example.hanium2023.domain.dto.user.UserAutoFeedBackResponse;
import com.example.hanium2023.domain.dto.user.UserAutoFeedbackRequest;
import com.example.hanium2023.domain.entity.BookMark;
import com.example.hanium2023.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final StationService stationService;
    private final BookMarkService bookMarkService;


    @PostMapping("/auto-feedback")
    public Response<UserAutoFeedBackResponse> doAutoFeedback(@RequestBody UserAutoFeedbackRequest userAutoFeedbackRequest){
        return Response.success(userService.doAutoFeedback(userAutoFeedbackRequest));
    }

    @GetMapping("/bookmarks")
    public List<BookMark> getAllBookmarks() {
        return bookMarkService.getAllBookmarks();
    }
}

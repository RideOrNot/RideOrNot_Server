package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.ressponse.Response;
import com.example.hanium2023.domain.dto.user.UserAutoFeedbackRequest;
import com.example.hanium2023.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @PostMapping("/auto-feedback")
    public Response<String> doFeedback(@RequestBody UserAutoFeedbackRequest userAutoFeedbackRequest){
        return Response.success(userService.doFeedback(userAutoFeedbackRequest));
    }
}

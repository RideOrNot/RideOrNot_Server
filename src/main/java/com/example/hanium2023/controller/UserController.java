package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.user.UserFeedbackRequest;
import com.example.hanium2023.repository.UserRepository;
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
    @PostMapping("/feedback")
    public String doFeedback(@RequestBody UserFeedbackRequest userFeedbackRequest){
        return userService.doFeedback(userFeedbackRequest);
    }
}

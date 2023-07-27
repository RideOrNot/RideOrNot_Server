package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.ressponse.Response;
import com.example.hanium2023.service.BookMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

import static com.example.hanium2023.domain.dto.ressponse.Response.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookMarkController {
    private final BookMarkService bookMarkService;

    @PostMapping("/insert") //북마크 추가
    public Response<?> insert(@RequestParam Long userId, @RequestParam Integer stationId) throws Exception {
        bookMarkService.insert(userId, stationId);
        return success(null);
    }

    @DeleteMapping("/delete") //북마크 해제
    public Response<?> delete(@RequestParam Long userId, @RequestParam Integer stationId) throws ChangeSetPersister.NotFoundException {
        bookMarkService.delete(userId, stationId);
        return success(null);
    }

    @GetMapping("/list/{userId}") // 북마크 조회
    public Response<?> getBookMarkList(@PathVariable("userId") Long userId){
        return Response.success(bookMarkService.getBookMarkList(userId));
    }

}



//RequestBody 어노테이션 -> @RequestParam 어노테이션 사용해야함?

    //북마크 추가
    /*public Response<?> insert(@RequestBody @Valid BookMarkRequest bookMarkRequest) throws Exception {
        System.out.println(bookMarkRequest.getUserId());
        bookMarkService.insert(bookMarkRequest);
        return success(null);
    }*/

    //북마크 해제
/*public Response<?> delete(@RequestBody @Valid BookMarkRequest bookMarkRequest) throws ChangeSetPersister.NotFoundException {
        bookMarkService.delete(bookMarkRequest);
        return success(null);
    }*/
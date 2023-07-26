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
    //같은역, 같은 아이디 동시에 들어가지 않게 해야함(지울 때 안지워짐)
    public Response<?> insert(@RequestParam Long userId, @RequestParam Integer stationId) throws Exception {
        bookMarkService.insert(userId, stationId);
        return success(null);
    }

    @DeleteMapping("/delete") //북마크 해제
    public Response<?> delete(@RequestParam Long userId, @RequestParam Integer stationId) throws ChangeSetPersister.NotFoundException {
        bookMarkService.delete(userId, stationId);
        return success(null);
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
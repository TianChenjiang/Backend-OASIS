package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.PictureBlService;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PictureController {
    @Autowired
    PictureBlService pictureBlService;

    @GetMapping("/picture/academic")
    public BasicResponse getAcademicRelationByAuthorId(@RequestParam(value = "id") String id) {
        return pictureBlService.getAcademicRelationByAuthorId(id);
    }
}

package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.TaskBlService;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {
    private TaskBlService taskBlService;
    @Autowired
    public TaskController(TaskBlService taskBlService) {
        this.taskBlService = taskBlService;
    }

    @GetMapping("/task/state")
    public BasicResponse getCrawlTask() {
        return taskBlService.getCrawlTask();
    }
}

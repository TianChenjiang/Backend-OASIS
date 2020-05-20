package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.TaskBlService;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TaskController {
    private TaskBlService taskBlService;
    @Autowired
    public TaskController(TaskBlService taskBlService) {
        this.taskBlService = taskBlService;
    }

    @GetMapping("/task/state")
    public BasicResponse getCrawlTask(@RequestParam(value = "filterKey") String filterKey,
                                      @RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return taskBlService.getCrawlTask(filterKey, date);
    }
}

package com.rubiks.backendoasis.model.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskState {
    private List<BriefProceeding> proceedings;
    private Date start_time;
    private Date end_time;
    @JsonProperty("is_finished")
    private boolean is_finished;
    private int paper_count;
    private int total_paper_num;
}

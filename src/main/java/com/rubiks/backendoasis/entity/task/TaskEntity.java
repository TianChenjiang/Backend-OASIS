package com.rubiks.backendoasis.entity.task;

import com.rubiks.backendoasis.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = Constant.TASK_COLLECTION)

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {
    private List<ProceedingEntity> proceedings;
    private Date start_time;
    private Date end_time;
    private boolean is_finished;
    private String description;
    private int paper_count;
    private int total_paper_num;
}

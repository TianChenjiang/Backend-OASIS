package com.rubiks.backendoasis.entity.task;

import com.rubiks.backendoasis.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collection = Constant.TASK_COLLECTION)

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {
    private List<ProceedingEntity> proceedings;
    @Field("start_time")
    private Date startTime;
    @Field("end_time")
    private Date endTime;
    @Field("is_finished")
    private boolean isFinished;
    private String description;
    @Field("paper_count")
    private int paperCount;
    @Field("total_paper_num")
    private int totalPaperNum;
}

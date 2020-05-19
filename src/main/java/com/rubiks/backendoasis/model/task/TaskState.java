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
    @Field("start_time")
    private Date startTime;
    @Field("end_time")
    private Date endTime;
    @JsonProperty("isFinished")
    @Field("is_finished")
    private boolean isFinished;
    @Field("paper_count")
    private int paperCount;
    @Field("total_paper_num")
    private int totalPaperNum;
}

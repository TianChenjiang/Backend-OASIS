package com.rubiks.backendoasis.entity.talents;

import com.rubiks.backendoasis.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = Constant.TALENTS_COLLECTION)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TalentsEntity {
    @Id
    private String id;

    @Field("field")
    private String field;

    @Field("count")
    private int count;

    @Field("experts")
    private List<ExpertsEntity> experts;

    @Field("index")
    private double index;
}

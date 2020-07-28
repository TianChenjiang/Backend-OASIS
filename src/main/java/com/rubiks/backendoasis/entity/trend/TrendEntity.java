package com.rubiks.backendoasis.entity.trend;

import com.rubiks.backendoasis.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = Constant.TREND_COLLECTION)
public class TrendEntity {
    @Field("year")
    List<Integer> years;
    @Field("matrix")
    List<List<Integer>> value;
    @Field("fields")
    List<String> keywords;
}

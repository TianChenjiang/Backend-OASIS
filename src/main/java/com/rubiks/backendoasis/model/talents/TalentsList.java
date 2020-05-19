package com.rubiks.backendoasis.model.talents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentsList {
    @Field("experts")
    private List<BriefTalents> talentsList;
}

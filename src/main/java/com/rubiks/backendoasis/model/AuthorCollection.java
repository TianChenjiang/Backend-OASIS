package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.entity.AuthorEntity;
import com.rubiks.backendoasis.entity.MetricsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorCollection {
    @Field("authors")
    private List<AuthorEntity> authorEntityList;
    @Field("metrics")
    private MetricsEntity metricsEntity;
}

package com.rubiks.backendoasis;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class TodoEntity {
    @Id
    @GeneratedValue
    private long id;

    @NonNull
    private String content;

    @NonNull
    private boolean isFinished;
}

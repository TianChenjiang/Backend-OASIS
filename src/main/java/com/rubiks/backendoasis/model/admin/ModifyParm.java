package com.rubiks.backendoasis.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyParm implements Serializable {
    private String src;
    private String desc;
}

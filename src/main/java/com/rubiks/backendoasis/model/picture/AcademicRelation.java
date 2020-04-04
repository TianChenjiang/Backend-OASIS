package com.rubiks.backendoasis.model.picture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcademicRelation {
    private List<Node> nodes;
    private List<Link> links;
}

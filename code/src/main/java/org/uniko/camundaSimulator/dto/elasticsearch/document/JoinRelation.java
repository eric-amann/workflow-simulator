package org.uniko.camundaSimulator.dto.elasticsearch.document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRelation {

    private String name;
    private Long parent;
}

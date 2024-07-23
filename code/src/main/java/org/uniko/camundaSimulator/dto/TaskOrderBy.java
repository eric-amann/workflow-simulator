package org.uniko.camundaSimulator.dto;

import io.camunda.operate.search.SortOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskOrderBy {

    private TaskSortFields fields;
    private SortOrder order;
}

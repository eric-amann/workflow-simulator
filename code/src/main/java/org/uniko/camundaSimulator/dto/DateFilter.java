package org.uniko.camundaSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class DateFilter {
    private LocalDateTime from;

    private LocalDateTime to;

}
package org.uniko.camundaSimulator.engine.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NumericVariableConfig extends ProcessVariableConfig{

    private double lowerBound;
    private double upperBound;
    private double mean;
    private double standardDeviation;
    private int decimalPlaces;
}

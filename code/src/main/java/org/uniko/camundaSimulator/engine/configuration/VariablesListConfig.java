package org.uniko.camundaSimulator.engine.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VariablesListConfig {

    List<NumericVariableConfig> numeric = new ArrayList<>();
    List<BooleanVariableConfig> bool = new ArrayList<>();
    List<StringVariableConfig> string = new ArrayList<>();
    List<ContextVariableConfig> context = new ArrayList<>();

    public List<ProcessVariableConfig> getVariables() {
        List<ProcessVariableConfig> variableConfigs = new ArrayList<>();
        variableConfigs.addAll(numeric);
        variableConfigs.addAll(bool);
        variableConfigs.addAll(string);
        variableConfigs.addAll(context);
        return variableConfigs;
    }
}

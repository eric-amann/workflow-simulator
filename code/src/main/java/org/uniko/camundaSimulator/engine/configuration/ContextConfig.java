package org.uniko.camundaSimulator.engine.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.uniko.camundaSimulator.engine.context.ContextApplicationType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContextConfig {

    private String id;
    private String filePath;
    private String timestampFormat;
    private int timestampColumn;

}

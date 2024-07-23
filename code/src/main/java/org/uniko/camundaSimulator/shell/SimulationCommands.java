package org.uniko.camundaSimulator.shell;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.uniko.camundaSimulator.service.CamundaConfigurationService;
import org.uniko.camundaSimulator.engine.configuration.SimulationConfig;
import org.uniko.camundaSimulator.service.SimulationService;

import java.io.File;

@ShellComponent
public class SimulationCommands {

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private CamundaConfigurationService camundaConfiguration;


    /**
     * This command is used to load a configuration file for a new simulation scenario.
     * @param configurationFilePath the file path for the configuration file
     * @return a command line message regarding the status of the simulation configuration
     */
    @ShellMethod(key = "load-config")
    public String loadConfiguration(@ShellOption String configurationFilePath) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            mapper.findAndRegisterModules();
            File configFile = new File(configurationFilePath);
            SimulationConfig[] configs = mapper.readValue(configFile, SimulationConfig[].class);

            for (SimulationConfig config : configs) {
                simulationService.addSimulation(config.createSimulation());
            }
            return "Successfully loaded simulation configuration file " + configFile.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return "Could not load configuration. Related error: " + e.getMessage();
        }
    }

    @ShellMethod(key = "start")
    public String startSimulation(@ShellOption String simulationId) {
        try {
            simulationService.startSimulation(simulationId);
            return "Started simulation " + simulationId;
        } catch (Exception e) {
            return "Could not start simulation. Related error: " + e.getMessage();
        }
    }

    @ShellMethod(key = "abort")
    public String abortSimulation(@ShellOption String simulationId) {
        try {
            simulationService.abortSimulation(simulationId);
            return "Aborted simulation " + simulationId;
        } catch (Exception e) {
            return "Could not abort simulation. Related error: " + e.getMessage();
        }
    }

    @ShellMethod(key = "setup")
    public String setup(@ShellOption String configurationFilePath) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            File configFile = new File(configurationFilePath);
            CamundaConfigurationService config = mapper.readValue(configFile, CamundaConfigurationService.class);
            camundaConfiguration.loadConfig(config);
            return "Setup complete";
        } catch (Exception e) {
            return "Could not connect to services. Related error: " + e.getMessage();
        }
    }

    @ShellMethod(key = "clientId")
    public String clientId(@ShellOption String clientId) {
        try {
            camundaConfiguration.setClientId(clientId);
            return "Saved client ID";
        } catch (Exception e) {
            return "Could not connect to services. Related error: " + e.getMessage();
        }
    }

    @ShellMethod(key = "clientSecret")
    public String clientSecret(@ShellOption String clientSecret) {
        try {
            camundaConfiguration.setClientSecret(clientSecret);
            return "Saved client secret";
        } catch (Exception e) {
            return "Could not connect to services. Related error: " + e.getMessage();
        }
    }
}

package org.uniko.camundaSimulator.service;

import org.springframework.stereotype.Service;
import org.uniko.camundaSimulator.engine.simulation.Simulation;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimulationService {

    private List<Simulation> simulations = new ArrayList<>();

    public void addSimulation(Simulation simulation) throws Exception {

        if (simulations.stream().anyMatch(sim -> sim.getId().equals(simulation.getId()))) {
            throw new Exception("Simulation with id " + simulation.getId() + " already exits");
        }
        simulations.add(simulation);
    }

    public void startSimulation(String simulationId) {
        Simulation builder = simulations.stream()
                .filter(sim -> sim.getId().equals(simulationId))
                .findFirst()
                .orElseThrow();

        builder.start();
    }

    public void abortSimulation(String simulationId) {
        Simulation builder = simulations.stream()
                .filter(sim -> sim.getId().equals(simulationId))
                .findFirst()
                .orElseThrow();

        builder.end();
    }
}

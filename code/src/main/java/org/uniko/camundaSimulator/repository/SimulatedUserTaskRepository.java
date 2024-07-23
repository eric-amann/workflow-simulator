package org.uniko.camundaSimulator.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.uniko.camundaSimulator.model.SimulatedUserTask;

@Repository
public interface SimulatedUserTaskRepository extends CrudRepository<SimulatedUserTask, Long> {
}

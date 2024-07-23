package org.uniko.camundaSimulator.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.uniko.camundaSimulator.model.SimulatedCase;

@Repository
public interface SimulatedCaseRepository extends CrudRepository<SimulatedCase, Long> {

    SimulatedCase getSimulatedCaseByProcessInstanceId(long processInstanceId);

    Iterable<SimulatedCase> findAllByProcessDefinitionKey(long processDefinitionKey);
}

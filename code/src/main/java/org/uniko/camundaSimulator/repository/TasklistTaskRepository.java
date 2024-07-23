package org.uniko.camundaSimulator.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.uniko.camundaSimulator.dto.elasticsearch.document.TasklistTask;

import java.util.Optional;

@Repository
public interface TasklistTaskRepository extends ElasticsearchRepository<TasklistTask, Long> {

    @Query("{" +
            "   \"bool\": {" +
            "       \"must\": [" +
            "           {" +
            "               \"match\": {" +
            "                   \"processInstanceId\": \"?0\"" +
            "               }" +
            "           }," +
            "           {" +
            "               \"match\": {" +
            "                   \"flowNodeInstanceId\": \"?1\"" +
            "               }" +
            "           }" +
            "       ]" +
            "    }" +
            "}")
    Optional<TasklistTask> getTasklistTaskByProcessInstanceIdAndFlowNodeInstanceId(
            long processInstanceKey,
            String flowNodeInstanceId
    );
}

package org.uniko.camundaSimulator.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.uniko.camundaSimulator.dto.elasticsearch.document.OperateFlowNodeInstance;

import java.util.Optional;

@Repository
public interface OperateFlowNodeInstanceRepository extends ElasticsearchRepository<OperateFlowNodeInstance, String> {

    @Query("{" +
            "   \"bool\": {" +
            "       \"must\": [" +
            "           {" +
            "               \"match\": {" +
            "                   \"processInstanceKey\": \"?0\"" +
            "               }" +
            "           }," +
            "           {" +
            "               \"match\": {" +
            "                   \"flowNodeId\": \"?1\"" +
            "               }" +
            "           }" +
            "       ]" +
            "    }" +
            "}")
    Optional<OperateFlowNodeInstance> getFlowNodeInstanceByProcessInstanceKeyAndFlowNodeId(
            long processInstanceKey,
            String flowNodeId
    );
}

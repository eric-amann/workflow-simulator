package org.uniko.camundaSimulator.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.uniko.camundaSimulator.dto.elasticsearch.document.OperateEvent;

import java.util.Optional;

@Repository
public interface OperateEventRepository extends ElasticsearchRepository<OperateEvent, String> {

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
    Optional<OperateEvent> getOperateEventByProcessInstanceKeyAndFlowNodeId(long processInstanceKey, String flowNodeId);
}

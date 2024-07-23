package org.uniko.camundaSimulator.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.uniko.camundaSimulator.dto.elasticsearch.document.OperateListView;

@Repository
public interface OperateListViewRepository extends ElasticsearchRepository<OperateListView, String> {

    @Query("{" +
            "   \"bool\": {" +
            "       \"must\": [" +
            "           {" +
            "               \"match\": {" +
            "                   \"processDefinitionKey\": \"?0\"" +
            "               }" +
            "           }," +
            "           {" +
            "               \"match\": {" +
            "                   \"processInstanceKey\": \"?1\"" +
            "               }" +
            "           }," +
            "           {" +
            "               \"exists\": {" +
            "                   \"field\": \"startDate\"" +
            "               }" +
            "           }" +
            "       ]" +
            "    }" +
            "}")
    OperateListView getListViewByProcessDefinitionKeyAndProcessInstanceKey(
            long processDefinitionKey,
            long processInstanceKey
    );
}

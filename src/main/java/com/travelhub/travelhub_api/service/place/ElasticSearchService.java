package com.travelhub.travelhub_api.service.place;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final ElasticsearchOperations operations;

    @Value("${elastic.place-index}")
    private String index;

    public void refresh() {
        String newIndexName = index + LocalDate.now();
        String oldIndexName = index + LocalDate.now().minusDays(7);

        createIndex(newIndexName);
        deleteIndex(oldIndexName);
    }

    private void createIndex(String name) {
        IndexOperations indexOps = operations.indexOps(IndexCoordinates.of(name));
        if (!indexOps.exists()) {
            indexOps.create();
            log.info("Index created: {}", name);
        }
    }

    private void deleteIndex(String name) {
        IndexOperations indexOps = operations.indexOps(IndexCoordinates.of(name));
        if (indexOps.exists()) {
            indexOps.delete();
            log.info("Index deleted: {}", name);
        }
    }

}

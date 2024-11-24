package com.travelhub.travelhub_api.data.elastic.repository;

import com.travelhub.travelhub_api.data.elastic.entity.TravelPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TravelRepository extends ElasticsearchRepository<TravelPlace, String> {
    Page<TravelPlace> findByPcNameContaining(String pcName, Pageable pageable);
}

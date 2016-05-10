package com.innvo.repository.search;

import com.innvo.domain.Reportparameter;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Reportparameter entity.
 */
public interface ReportparameterSearchRepository extends ElasticsearchRepository<Reportparameter, Long> {
}

package com.bahar.repository.model.base;

/**
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface ElasticSearchModel {

    /**
     * 在ElasticSearch中的Index
     * @return
     */
    String elasticSearchIndex();

    /**
     * 在ElasticSearch中的Type
     * @return
     */
    String elasticSearchType();

    /**
     * 获取ID
     * @return
     */
    String elasticSearchId();
}

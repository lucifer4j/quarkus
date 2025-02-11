package io.quarkus.hibernate.search.orm.elasticsearch.runtime.devconsole;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.hibernate.search.mapper.orm.entity.SearchIndexedEntity;
import org.hibernate.search.mapper.orm.mapping.SearchMapping;

import io.quarkus.arc.Arc;
import io.quarkus.hibernate.search.orm.elasticsearch.runtime.HibernateSearchElasticsearchRuntimeConfig;
import io.quarkus.hibernate.search.orm.elasticsearch.runtime.devconsole.HibernateSearchSupplier.DevUiIndexedEntity;

public class HibernateSearchSupplier implements Supplier<List<DevUiIndexedEntity>> {

    private final HibernateSearchElasticsearchRuntimeConfig runtimeConfig;

    HibernateSearchSupplier(HibernateSearchElasticsearchRuntimeConfig runtimeConfig) {
        this.runtimeConfig = runtimeConfig;
    }

    @Override
    public List<DevUiIndexedEntity> get() {
        if (!isEnabled()) {
            return Collections.emptyList();
        }
        SearchMapping mapping = searchMapping();
        if (mapping == null) {
            return Collections.emptyList();
        }
        return mapping.allIndexedEntities().stream().map(DevUiIndexedEntity::new).sorted()
                .collect(Collectors.toList());
    }

    private boolean isEnabled() {
        return runtimeConfig.defaultPersistenceUnit.enabled;
    }

    public static SearchMapping searchMapping() {
        return Arc.container().instance(SearchMapping.class).get();
    }

    public static class DevUiIndexedEntity implements Comparable<DevUiIndexedEntity> {

        public final String jpaName;
        public final String javaClass;

        DevUiIndexedEntity(SearchIndexedEntity<?> searchIndexedEntity) {
            this.jpaName = searchIndexedEntity.jpaName();
            this.javaClass = searchIndexedEntity.javaClass().getName();
        }

        @Override
        public int compareTo(DevUiIndexedEntity o) {
            return this.jpaName.compareTo(o.jpaName);
        }
    }
}

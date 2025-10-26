package com.rejs.orm.session.registry;

import com.rejs.orm.annotations.Entity;
import com.rejs.orm.session.metadata.EntityMetadata;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EntityMetadataRegistry {
    private final String basePackage = "com.rejs.orm";
    private final Map<Class<?>, EntityMetadata> metadataCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initialize(){
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

        Set<BeanDefinition> candiateCompnents = scanner.findCandidateComponents(basePackage);

        for (BeanDefinition bd : candiateCompnents){
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                EntityMetadata metadata = EntityMetadata.from(clazz);
                metadataCache.put(clazz, metadata);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public EntityMetadata get(Class<?> clazz){
        return metadataCache.get(clazz);
    }
}

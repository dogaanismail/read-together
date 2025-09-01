package org.readtogether.feedback.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.readtogether.common.mapper.BaseMapper;
import org.readtogether.feedback.entity.FeatureRequestEntity;
import org.readtogether.feedback.model.FeatureRequest;

@Mapper
public interface FeatureRequestEntityToFeatureRequestMapper extends BaseMapper<FeatureRequestEntity, FeatureRequest> {

    @Override
    @Mapping(target = "id", expression = "java(source.getId().toString())")
    @Mapping(target = "authorId", expression = "java(source.getAuthorId().toString())")
    FeatureRequest map(FeatureRequestEntity source);

    static FeatureRequestEntityToFeatureRequestMapper initialize() {
        return Mappers.getMapper(FeatureRequestEntityToFeatureRequestMapper.class);
    }

}
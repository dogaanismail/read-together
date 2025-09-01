package org.readtogether.feedback.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.readtogether.common.mapper.BaseMapper;
import org.readtogether.feedback.entity.FeatureRequestEntity;
import org.readtogether.feedback.model.response.FeatureRequestResponse;

@Mapper
public interface FeatureRequestEntityToFeatureRequestResponseMapper extends BaseMapper<FeatureRequestEntity, FeatureRequestResponse> {

    @Override
    @Mapping(target = "id", expression = "java(source.getId().toString())")
    @Mapping(target = "authorId", expression = "java(source.getAuthorId().toString())")
    FeatureRequestResponse map(FeatureRequestEntity source);

    static FeatureRequestEntityToFeatureRequestResponseMapper initialize() {
        return Mappers.getMapper(FeatureRequestEntityToFeatureRequestResponseMapper.class);
    }

}
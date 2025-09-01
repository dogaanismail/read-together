package org.readtogether.feedback.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.readtogether.common.mapper.BaseMapper;
import org.readtogether.feedback.entity.BugReportEntity;
import org.readtogether.feedback.model.response.BugReportResponse;

@Mapper
public interface BugReportEntityToBugReportResponseMapper extends BaseMapper<BugReportEntity, BugReportResponse> {

    @Override
    @Mapping(target = "id", expression = "java(source.getId().toString())")
    @Mapping(target = "reporterId", expression = "java(source.getReporterId().toString())")
    BugReportResponse map(BugReportEntity source);

    static BugReportEntityToBugReportResponseMapper initialize() {
        return Mappers.getMapper(BugReportEntityToBugReportResponseMapper.class);
    }

}
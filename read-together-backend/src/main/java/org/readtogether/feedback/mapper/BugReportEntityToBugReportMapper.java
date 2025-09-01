package org.readtogether.feedback.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.readtogether.common.mapper.BaseMapper;
import org.readtogether.feedback.entity.BugReportEntity;
import org.readtogether.feedback.model.BugReport;

@Mapper
public interface BugReportEntityToBugReportMapper extends BaseMapper<BugReportEntity, BugReport> {

    @Override
    @Mapping(target = "id", expression = "java(source.getId().toString())")
    @Mapping(target = "reporterId", expression = "java(source.getReporterId().toString())")
    BugReport map(BugReportEntity source);

    static BugReportEntityToBugReportMapper initialize() {
        return Mappers.getMapper(BugReportEntityToBugReportMapper.class);
    }

}
package org.readtogether.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.readtogether.common.mapper.BaseMapper;
import org.readtogether.user.model.User;
import org.readtogether.user.entity.UserEntity;

@Mapper
public interface UserEntityToUserMapper extends BaseMapper<UserEntity, User> {

    @Override
    @Mapping(target = "id", expression = "java(source.getId().toString())")
    User map(UserEntity source);

    static UserEntityToUserMapper initialize() {
        return Mappers.getMapper(UserEntityToUserMapper.class);
    }

}

package org.readtogether.user.model.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.readtogether.common.mapper.BaseMapper;
import org.readtogether.common.model.auth.User;
import org.readtogether.user.entity.UserEntity;

@Mapper
public interface UserEntityToUserMapper extends BaseMapper<UserEntity, User> {

    @Override
    User map(UserEntity source);

    static UserEntityToUserMapper initialize() {
        return Mappers.getMapper(UserEntityToUserMapper.class);
    }

}

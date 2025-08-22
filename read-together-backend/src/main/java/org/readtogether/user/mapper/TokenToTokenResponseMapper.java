package org.readtogether.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.readtogether.common.mapper.BaseMapper;
import org.readtogether.security.model.Token;
import org.readtogether.security.model.response.TokenResponse;

@Mapper
public interface TokenToTokenResponseMapper extends BaseMapper<Token, TokenResponse> {

    @Override
    TokenResponse map(Token source);

    static TokenToTokenResponseMapper initialize() {
        return Mappers.getMapper(TokenToTokenResponseMapper.class);
    }

}

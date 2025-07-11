package com.webinarnttdata.auth.domain;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDTO toDto(User user); //Esto es de Entidad a DTO
    User toEntity(UserDTO userDTO);// Esto es de DTO a Entidad
}

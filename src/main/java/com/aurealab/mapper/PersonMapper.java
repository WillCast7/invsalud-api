package com.aurealab.mapper;

import com.aurealab.dto.PersonDTO;
import com.aurealab.model.aurea.entity.PersonEntity;

public class PersonMapper {
    private PersonMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static PersonDTO toDto(PersonEntity entity) {
        if (entity == null) return null;

        return new PersonDTO(
                entity.getId(),
                entity.getDniType(),
                entity.getDniNumber(),
                entity.getNames(),
                entity.getSurnames(),
                entity.getPhoneNumber(),
                entity.getAddress(),
                entity.getBirthDate()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static PersonEntity toEntity(PersonDTO dto) {
        if (dto == null) return null;

        PersonEntity entity = new PersonEntity();
        entity.setId(dto.getId());
        entity.setDniType(dto.getDniType());
        entity.setDniNumber(dto.getDniNumber());
        entity.setNames(dto.getNames());
        entity.setSurnames(dto.getSurNames());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setAddress(dto.getAddress());
        entity.setBirthDate(dto.getBirthDate());
        return entity;
    }
}

package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Mpa;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MpaMapper {

    public static Mpa mapToMpa(NewMpaRequest request) {
        Mpa mpa = new Mpa();
        mpa.setId(request.getId());
        mpa.setName(request.getName());
        return mpa;
    }

    public static MpaDto mapToMpaDto(Mpa mpa) {
        MpaDto dto = new MpaDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        return dto;
    }

    public static Mpa updateMpaFields(Mpa mpa, UpdateMpaRequest request) {
        if (request.hasName()) {
            mpa.setName(request.getName());
        }
        return mpa;
    }
}

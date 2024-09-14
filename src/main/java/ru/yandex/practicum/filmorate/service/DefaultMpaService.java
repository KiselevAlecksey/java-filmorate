package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.interfaces.MpaRepository;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.service.interfaces.MpaService;
import static ru.yandex.practicum.filmorate.utils.ModelConverter.*;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultMpaService implements MpaService {

    @Autowired
    @Qualifier("JdbcMpaRepository")
    private final MpaRepository mpaRepository;

    @Override
    public MpaDto getById(Integer id) {
        if (id == null ||
                (id < 0 || id > 6 || mpaRepository.findById(id).isEmpty())) {
            throw new NotFoundException("Рейтинг не найден");
        }

        return mpaRepository.findById(id).orElse(null);
    }

    @Override
    public Collection<MpaDto> getAll() {
        return mpaRepository.values().stream()
                .map(MpaMapper::mapToMpaDto)
                .collect(Collectors.toList()
                );
    }
}

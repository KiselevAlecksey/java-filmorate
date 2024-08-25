package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultGenreService implements GenreService {

    @Autowired
    @Qualifier("JdbcGenreRepository")
    private final GenreRepository genreRepository;

    @Override
    public GenreDto getById(Integer id) {
        if (id == null || !genreRepository.getAllGenreIds().stream().anyMatch(Id -> Id.equals(id))) {
            throw new NotFoundException("Жанр не найден");
        }

        return genreRepository.findById(id).map(GenreMapper::mapToGenreDto).orElse(null);
    }

    @Override
    public Collection<GenreDto> getAll() {
        return genreRepository.values().stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList()
                );
    }
}

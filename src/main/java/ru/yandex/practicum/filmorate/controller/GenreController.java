package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.service.interfaces.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Collection<GenreDto> findAll() {
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public GenreDto getGenre(@PathVariable int id) {
        log.error("Genre get name by id {} start", id);
        GenreDto genreDto = genreService.getById(id);
        log.error("Genre get name by id {} complete", id);
        return genreDto;
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private final GenreService genreService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<GenreDto> findAll() {
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GenreDto getGenre(@PathVariable int id) {
        logger.error("Genre get name by id {} start", id);
        GenreDto genreDto = genreService.getById(id);
        logger.error("Genre get name by id {} complete", id);
        return genreDto;
    }
}

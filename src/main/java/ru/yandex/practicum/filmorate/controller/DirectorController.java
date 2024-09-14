package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.service.interfaces.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<DirectorDto> getAll() {
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public DirectorDto getById(@PathVariable Long id) {
        log.error("Genre get name by id {} start", id);
        DirectorDto directorDto = directorService.getById(id);
        log.error("Genre get name by id {} complete", id);
        return directorDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto create(@RequestBody NewDirectorRequest request) {
            log.error("Director create {} start", request);
            DirectorDto created = directorService.create(request);
            log.error("Created director is {}", created.getName());
            return created;
    }

    @PutMapping
    public DirectorDto update(@RequestBody UpdateDirectorRequest request) {
        log.error("Director update {} start", request);
        DirectorDto updated = directorService.update(request);
        log.error("Updated director is {} complete", updated.getName());
        return updated;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.error("Remove director id {}", id);
        directorService.delete(id);
        log.error("Removed director  id {}", id);
    }
}
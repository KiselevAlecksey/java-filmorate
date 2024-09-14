package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.service.interfaces.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<MpaDto> findAll() {
        return mpaService.getAll();
    }

    @GetMapping("/{id}")
    public MpaDto getMpa(@PathVariable int id) {
        log.error("Mpa get name by id {} start", id);
        MpaDto mpaDto = mpaService.getById(id);
        log.error("Mpa get name by id {} complete", id);
        return mpaDto;
    }
}

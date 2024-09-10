package ru.yandex.practicum.filmorate.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.interfaces.DirectorRepository;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.interfaces.DirectorService;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDirectorService implements DirectorService {

    @Autowired
    @Qualifier("JdbcDirectorRepository")
    private final DirectorRepository directorRepository;

    @Override
    public DirectorDto getById(Long id) {

        if (id == null) {
            throw new NotFoundException("Режиссер не найден");
        }

        return directorRepository.getById(id)
                .map(DirectorMapper::mapToDirectorDto).orElseThrow(
                        () -> new NotFoundException("Режиссер не найден"));
    }

    @Override
    public Collection<DirectorDto> getAll() {
        return directorRepository.values().stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList()
                );
    }

    @Override
    public DirectorDto create(NewDirectorRequest request) {

        Director putDirector = DirectorMapper.mapToDirector(request);

        putDirector = directorRepository.create(putDirector);

        return DirectorMapper.mapToDirectorDto(putDirector);
    }

    @Override
    public DirectorDto update(UpdateDirectorRequest request) {

        if (request.getId() == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (directorRepository.getById(request.getId()).isPresent()) {
            if (request.getName().isBlank()) {

                throw new ConditionsNotMetException(
                        "Поле имя не может быть пустым"
                );
            }
            Director updatedDirector = directorRepository.getById(request.getId())
                    .map(director -> DirectorMapper.updateDirectorFields(director, request))
                    .orElseThrow(() -> new NotFoundException("Режиссер не найден"));

            directorRepository.update(updatedDirector);
            return DirectorMapper
                    .mapToDirectorDto(updatedDirector.getName().isBlank()
                            ? updatedDirector.toBuilder().id(request.getId()).name(request.getName()).build() : updatedDirector);
        }
        throw new NotFoundException("Режиссер с id = " + request.getId() + " не найден");
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (directorRepository.getById(id).isPresent()) {
            return directorRepository.delete(id);
        }

        throw new NotFoundException(
                "Режиссер с id = " + id + " не найден"
        );
    }
}
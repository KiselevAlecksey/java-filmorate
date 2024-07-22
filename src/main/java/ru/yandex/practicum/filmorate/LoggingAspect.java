package ru.yandex.practicum.filmorate;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* ru.yandex.practicum.filmorate.service.FilmController.findAll(..))")
    public void logBeforeFilmFindAll(JoinPoint joinPoint) {
        logger.trace("Method execution started: " + joinPoint.getSignature());
    }

    @After("execution(* ru.yandex.practicum.filmorate.service.FilmController.findAll(..))")
    public void logAfterFilmFindAll(JoinPoint joinPoint) {
        logger.trace("Method execution finished: " + joinPoint.getSignature());
    }

    @Before("execution(* ru.yandex.practicum.filmorate.service.FilmController.add(..))")
    public void logBeforeAdd(JoinPoint joinPoint) {
        logger.trace("Method add started: " + joinPoint.getSignature());
    }

    @After("execution(* ru.yandex.practicum.filmorate.service.FilmController.add(..))")
    public void logAfterAdd(JoinPoint joinPoint) {
        logger.trace("Method add finished: " + joinPoint.getSignature());
    }

    @Before("execution(* ru.yandex.practicum.filmorate.service.FilmController.update(..))")
    public void logBeforeFilmUpdate(JoinPoint joinPoint) {
        logger.trace("Method update started: " + joinPoint.getSignature());
    }

    @After("execution(* ru.yandex.practicum.filmorate.service.FilmController.update(..))")
    public void logAfterFilmUpdate(JoinPoint joinPoint) {
        logger.trace("Method update finished: " + joinPoint.getSignature());
    }

    @Before("execution(* ru.yandex.practicum.filmorate.service.UserController.findAll(..))")
    public void logBeforeUserFindAll(JoinPoint joinPoint) {
        logger.trace("Method execution started: " + joinPoint.getSignature());
    }

    @After("execution(* ru.yandex.practicum.filmorate.service.UserController.findAll(..))")
    public void logAfterUserFindAll(JoinPoint joinPoint) {
        logger.trace("Method execution finished: " + joinPoint.getSignature());
    }

    @Before("execution(* ru.yandex.practicum.filmorate.service.UserController.add(..))")
    public void logBeforeCreate(JoinPoint joinPoint) {
        logger.trace("Method add started: " + joinPoint.getSignature());
    }

    @After("execution(* ru.yandex.practicum.filmorate.service.UserController.add(..))")
    public void logAfterCreate(JoinPoint joinPoint) {
        logger.trace("Method add finished: " + joinPoint.getSignature());
    }

    @Before("execution(* ru.yandex.practicum.filmorate.service.UserController.update(..))")
    public void logBeforeUserUpdate(JoinPoint joinPoint) {
        logger.trace("Method update started: " + joinPoint.getSignature());
    }

    @After("execution(* ru.yandex.practicum.filmorate.service.UserController.update(..))")
    public void logAfterUserUpdate(JoinPoint joinPoint) {
        logger.trace("Method update finished: " + joinPoint.getSignature());
    }
}

package ru.kazenin.gcore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String name) {
        super("Пользователь с именем " + name + " уже существует!");
    }
}

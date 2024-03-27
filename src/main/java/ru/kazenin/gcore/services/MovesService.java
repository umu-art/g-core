package ru.kazenin.gcore.services;

import ru.kazenin.model.Move;

public interface MovesService {

    void move(Move move);

    int getTime();
}

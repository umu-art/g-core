package ru.kazenin.gcore.services;

import ru.kazenin.model.GameObject;

import java.util.List;

public interface MapService {
    List<List<GameObject>> getMap();

    List<List<GameObject>> directGetMap();

    void generateMap();
}

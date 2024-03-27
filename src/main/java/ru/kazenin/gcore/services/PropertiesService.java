package ru.kazenin.gcore.services;

import ru.kazenin.model.GameProperties;

public interface PropertiesService {

    GameProperties getProperties();

    void setProperties(GameProperties gameProperties);
}

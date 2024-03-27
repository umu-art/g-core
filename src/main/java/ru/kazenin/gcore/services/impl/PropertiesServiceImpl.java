package ru.kazenin.gcore.services.impl;

import org.springframework.stereotype.Service;
import ru.kazenin.gcore.services.PropertiesService;
import ru.kazenin.model.GameProperties;

@Service
public class PropertiesServiceImpl implements PropertiesService {
    private GameProperties gameProperties = new GameProperties();

    @Override
    public GameProperties getProperties() {
        return gameProperties;
    }

    @Override
    public void setProperties(GameProperties gameProperties) {
        this.gameProperties = gameProperties;
    }
}

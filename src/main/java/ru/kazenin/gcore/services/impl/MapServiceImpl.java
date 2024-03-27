package ru.kazenin.gcore.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kazenin.gcore.game.MapUtils;
import ru.kazenin.gcore.services.MapService;
import ru.kazenin.gcore.services.PropertiesService;
import ru.kazenin.gcore.services.UsersService;
import ru.kazenin.model.GameObject;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {
    private final PropertiesService propertiesService;
    private final UsersService usersService;
    private List<List<GameObject>> map;

    @Override
    public List<List<GameObject>> getMap() {
        var userName = usersService.getUserName();

        if (!MapUtils.isUserAlive(map, userName) ||
                propertiesService.getProperties().getAllVisible()) {
            return map;
        }

        return MapUtils.hideMap(map, userName);
    }

    @Override
    public List<List<GameObject>> directGetMap() {
        return map;
    }

    @Override
    public void generateMap() {
        map = MapUtils.generateMap(propertiesService.getProperties(),
                usersService.getAllUsers());

    }
}

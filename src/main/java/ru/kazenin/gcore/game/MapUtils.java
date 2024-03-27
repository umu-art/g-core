package ru.kazenin.gcore.game;

import ru.kazenin.model.GameObject;
import ru.kazenin.model.GameObjectType;
import ru.kazenin.model.GameProperties;
import ru.kazenin.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class MapUtils {

    public static List<List<GameObject>> generateMap(GameProperties gameProperties, List<User> users) {
        var map = new ArrayList<List<GameObject>>();
        var random = new Random();

        int sum = 100 + gameProperties.getCityDensity() + gameProperties.getMountainDensity() + gameProperties.getMudDensity();

        for (int i = 0; i < gameProperties.getMapHeight(); i++) {
            var mapRow = new ArrayList<GameObject>();
            for (int j = 0; j < gameProperties.getMapWidth(); j++) {
                int g = random.nextInt(0, sum);
                if (g < 100) {
                    mapRow.add(new GameObject()
                            .type(GameObjectType.FREE)
                            .army(0));
                } else if (g < 100 + gameProperties.getCityDensity()) {
                    mapRow.add(new GameObject()
                            .type(GameObjectType.CITY)
                            .army(40 + random.nextInt(0, 10)));
                } else if (g < 100 + gameProperties.getCityDensity() + gameProperties.getMountainDensity()) {
                    mapRow.add(new GameObject()
                            .type(GameObjectType.MOUNTAIN));
                } else {
                    mapRow.add(new GameObject()
                            .type(GameObjectType.MUD)
                            .army(0));
                }
            }
            map.add(mapRow);
        }

        for (User user : users) {
            var x = random.nextInt(0, gameProperties.getMapWidth());
            var y = random.nextInt(0, gameProperties.getMapWidth());
            var pixel = map.get(x).get(y);
            pixel.setType(GameObjectType.CENTER);
            pixel.setOwnerName(user.getName());
            pixel.setArmy(10);
        }

        // TODO: dfs with available path

        return map;
    }

    public static List<List<GameObject>> hideMap(List<List<GameObject>> map, String user) {
        var hiddenMap = new ArrayList<List<GameObject>>();

        for (int i = 0; i < map.size(); i++) {
            var row = new ArrayList<GameObject>();
            for (int j = 0; j < map.get(i).size(); j++) {
                if (checkIsVisible(map, i, j, user)) {
                    row.add(map.get(i).get(j));
                } else {
                    row.add(new GameObject()
                            .type(switch (map.get(i).get(j).getType()) {
                                case CITY, CENTER, MOUNTAIN -> GameObjectType.UNKNOWN_BUILDING;
                                default -> GameObjectType.UNKNOWN_FREE;
                            })
                            .army(0));
                }
            }
            hiddenMap.add(row);
        }

        return hiddenMap;
    }

    private static boolean checkIsVisible(List<List<GameObject>> map, int x, int y, String user) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (x + i < 0 || x + i >= map.size() || y + j < 0 || y + j >= map.get(x + i).size()) {
                    continue;
                }

                if (user.equals(map.get(x + i).get(y + j).getOwnerName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int countAlive(List<List<GameObject>> map) {
        var aliveUsers = new HashSet<String>();
        map.forEach(row -> row.forEach(pixel -> aliveUsers.add(pixel.getOwnerName())));
        aliveUsers.remove(null);
        return aliveUsers.size();
    }

    public static boolean isUserAlive(List<List<GameObject>> map, String user) {
        for (var row : map) {
            for (var pixel : row) {
                if (user.equals(pixel.getOwnerName())) {
                    return true;
                }
            }
        }
        return false;
    }
}

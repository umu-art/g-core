package ru.kazenin.gcore.game;

import lombok.extern.slf4j.Slf4j;
import ru.kazenin.gcore.auth.AuthHolder;
import ru.kazenin.model.Direction;
import ru.kazenin.model.GameObject;
import ru.kazenin.model.GameObjectType;
import ru.kazenin.model.GameProperties;
import ru.kazenin.model.Move;
import ru.kazenin.model.User;

import java.util.List;
import java.util.Objects;

@Slf4j
public class MoveUtils {

    public synchronized static void executeMove(List<List<GameObject>> map, GameProperties properties, Move move, String name) {
        log.info("Move: {} from: {}", move, AuthHolder.token.get());

        var pixel = map.get(move.getFrom().getY()).get(move.getFrom().getX());

        if (!Objects.equals(pixel.getOwnerName(), name)) {
            throw new IllegalStateException("Чужая клетка");
        }

        if (move.getCount() == null || move.getCount() <= 0) {
            move.setCount(pixel.getArmy() - 1);
        }

        if (pixel.getArmy() <= move.getCount()) {
            throw new IllegalStateException("Много двигаешь");
        }

        var mapHeight = properties.getMapHeight();
        var mapWidth = properties.getMapWidth();
        if (move.getFrom().getX() == 0 && move.getDirection() == Direction.LEFT ||
                move.getFrom().getX() == mapWidth - 1 && move.getDirection() == Direction.RIGHT ||
                move.getFrom().getY() == 0 && move.getDirection() == Direction.UP ||
                move.getFrom().getY() == mapHeight - 1 && move.getDirection() == Direction.DOWN) {
            throw new IllegalStateException("Выход за границы карты");
        }

        var nextPixel = switch (move.getDirection()) {
            case UP -> map.get(move.getFrom().getY() - 1).get(move.getFrom().getX());
            case DOWN -> map.get(move.getFrom().getY() + 1).get(move.getFrom().getX());
            case LEFT -> map.get(move.getFrom().getY()).get(move.getFrom().getX() - 1);
            case RIGHT -> map.get(move.getFrom().getY()).get(move.getFrom().getX() + 1);
        };

        switch (nextPixel.getType()) {
            case FREE, CITY, CENTER, MUD -> {
                pixel.setArmy(pixel.getArmy() - move.getCount());

                if (name.equals(nextPixel.getOwnerName())) {
                    nextPixel.setArmy(nextPixel.getArmy() + move.getCount());
                } else {
                    if (nextPixel.getArmy() >= move.getCount()) {
                        nextPixel.setArmy(nextPixel.getArmy() - move.getCount());
                    } else {
                        // Захват клетки
                        int armyWas = nextPixel.getArmy();
                        processCenterCapulet(map, nextPixel, name);

                        nextPixel.setOwnerName(name);
                        nextPixel.setArmy(move.getCount() - armyWas);
                    }
                }
            }

            case MOUNTAIN -> throw new IllegalStateException("Нельзя ходить на гору");
        }
    }

    private static void processCenterCapulet(List<List<GameObject>> map, GameObject pixel, String newOwner) {
        if (pixel.getType() != GameObjectType.CENTER) {
            return;
        }

        var lastOwner = pixel.getOwnerName();
        pixel.setType(GameObjectType.CITY);

        map.forEach(row -> row.forEach(tp -> {
            if (lastOwner.equals(tp.getOwnerName())) {
                tp.setOwnerName(newOwner);
                tp.setArmy(Math.max(1, tp.getArmy() / 2));
            }
        }));
    }

    public static void updateMapFast(List<List<GameObject>> map) {
        fillArmyCities(map);
    }

    private static void fillArmyCities(List<List<GameObject>> map) {
        map.forEach(row -> row.forEach(pixel -> {
            if (pixel.getOwnerName() != null &&
                    (pixel.getType() == GameObjectType.CITY ||
                            pixel.getType() == GameObjectType.CENTER)) {
                pixel.setArmy(pixel.getArmy() + 1);
            }

            if (pixel.getOwnerName() != null &&
                    pixel.getType() == GameObjectType.MUD) {
                pixel.setArmy(pixel.getArmy() - 1);
                if (pixel.getArmy() <= 0) {
                    pixel.setArmy(0);
                    pixel.setOwnerName(null);
                }
            }
        }));
    }

    public static void updateMapSlow(List<List<GameObject>> map, List<User> users) {
        fillArmyAll(map);
        cleanInactive(map, users);

    }

    private static void cleanInactive(List<List<GameObject>> map, List<User> users) {
        map.forEach(row -> row.forEach(pixel -> {
            if (pixel.getOwnerName() != null &&
                    users.stream().noneMatch(user -> user.getName().equals(pixel.getOwnerName()))) {
                pixel.setOwnerName(null);
            }
        }));
    }

    private static void fillArmyAll(List<List<GameObject>> map) {
        map.forEach(row -> row.forEach(pixel -> {
            if (pixel.getOwnerName() != null && pixel.getType() == GameObjectType.FREE) {
                pixel.setArmy(pixel.getArmy() + 1);
            }
        }));
    }
}

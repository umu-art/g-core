package ru.kazenin.gcore.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kazenin.gcore.services.MapService;
import ru.kazenin.gcore.services.MovesService;
import ru.kazenin.gcore.services.PhaseService;
import ru.kazenin.gcore.services.StatisticService;
import ru.kazenin.gcore.services.UsersService;
import ru.kazenin.model.GameObject;
import ru.kazenin.model.GameObjectType;
import ru.kazenin.model.GamePhase;
import ru.kazenin.model.GameStats;
import ru.kazenin.model.UserGameStat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final MapService mapService;
    private final UsersService usersService;
    private final MovesService movesService;
    private final PhaseService phaseService;

    private final GameStats gameStats = new GameStats();

    @Override
    public GameStats getStatistic() {
        return gameStats;
    }

    @Scheduled(fixedDelay = 1000)
    private void updateStatistic() {
        if (phaseService.getGame() != GamePhase.PLAY) {
            return;
        }

        var newGameStats = new ArrayList<UserGameStat>();

        usersService.getAllUsers().forEach(user -> {
            var forUser = getForUser(user.getName());

            newGameStats.add(new UserGameStat()
                    .user(user)
                    .isAlive(!forUser.isEmpty())
                    .army(forUser.stream().mapToInt(GameObject::getArmy).sum())
                    .map(forUser.size())
                    .cities((int) forUser.stream().filter(gameObject ->
                            gameObject.getType() == GameObjectType.CENTER ||
                                    gameObject.getType() == GameObjectType.CITY).count()));
        });

        gameStats.setTime(movesService.getTime());
        gameStats.setUsers(newGameStats);
    }

    private List<GameObject> getForUser(String user) {
        return mapService.directGetMap().stream().flatMap(Collection::stream)
                .filter(gameObject -> user.equals(gameObject.getOwnerName()))
                .toList();
    }
}

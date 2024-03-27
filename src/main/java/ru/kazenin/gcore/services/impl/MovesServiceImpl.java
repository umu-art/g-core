package ru.kazenin.gcore.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kazenin.gcore.game.MapUtils;
import ru.kazenin.gcore.game.MoveUtils;
import ru.kazenin.gcore.services.MapService;
import ru.kazenin.gcore.services.MovesService;
import ru.kazenin.gcore.services.PhaseService;
import ru.kazenin.gcore.services.PropertiesService;
import ru.kazenin.gcore.services.UsersService;
import ru.kazenin.model.GamePhase;
import ru.kazenin.model.Move;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovesServiceImpl implements MovesService {
    private final UsersService usersService;
    private final PhaseService phaseService;
    private final MapService mapService;
    private final PropertiesService propertiesService;

    private final AtomicInteger actualMove = new AtomicInteger(0);
    private final AtomicInteger skipped = new AtomicInteger(0);

    @Scheduled(fixedRate = 100)
    private void update() {
        var timeMultiple = 5 + (100 - propertiesService.getProperties().getGameSpeed()) / 2;

        switch (phaseService.getGame()) {
            case PLAY -> {
                if (skipped.incrementAndGet() >= timeMultiple) {
                    log.info("Move {}", (actualMove.get() + 1));
                    MoveUtils.updateMapFast(mapService.directGetMap());
                    if (actualMove.incrementAndGet() % 10 == 0) {
                        MoveUtils.updateMapSlow(mapService.directGetMap(), usersService.getAllUsers());

                        var alive = MapUtils.countAlive(mapService.directGetMap());
                        log.info("Игра идет, живых игроков: {}", alive);
                        if (alive <= 1) {
                            phaseService.directStopGame();
                        }
                    }
                    skipped.set(0);
                }
            }
            case WAIT -> actualMove.set(0);
        }
    }

    @Override
    public void move(Move move) {
        if (phaseService.getGame() != GamePhase.PLAY) {
            throw new IllegalStateException("Game is not started");
        }
        waitMove();
        MoveUtils.executeMove(mapService.directGetMap(),
                propertiesService.getProperties(),
                move,
                usersService.getUserName());
    }

    @Override
    public int getTime() {
        if (phaseService.getGame() == GamePhase.PLAY) {
            return actualMove.get();
        }
        return 0;
    }

    private void waitMove() {
        var start = actualMove.get();
        while (start == actualMove.get()) {
            Thread.onSpinWait();
        }
    }
}

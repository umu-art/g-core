package ru.kazenin.gcore.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kazenin.gcore.auth.AuthHolder;
import ru.kazenin.gcore.exceptions.ForbiddenException;
import ru.kazenin.gcore.services.MapService;
import ru.kazenin.gcore.services.PhaseService;
import ru.kazenin.gcore.services.UsersService;
import ru.kazenin.model.GamePhase;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhaseServiceImpl implements PhaseService {
    private final UsersService usersService;
    private final MapService mapService;

    private GamePhase gamePhase = GamePhase.WAIT;

    @Override
    public GamePhase getGame() {
        return gamePhase;
    }

    @Override
    public void startGame() {
        if (!usersService.isGameAdmin(AuthHolder.token.get()) ||
                gamePhase != GamePhase.WAIT ||
                usersService.getNumberOfUsers() < 2) {
            throw new ForbiddenException("startGame");
        }

        mapService.generateMap();
        gamePhase = GamePhase.PLAY;
    }

    @Override
    public void stopGame() {
        if (!usersService.isGameAdmin(AuthHolder.token.get()) ||
                gamePhase != GamePhase.PLAY) {
            throw new ForbiddenException("stopGame");
        }

        gamePhase = GamePhase.WAIT;
    }

    @Override
    public void directStopGame() {
        gamePhase = GamePhase.WAIT;
    }

    @Scheduled(fixedDelay = 5000)
    private void stopGameIfNoUsers() {
        if (usersService.getNumberOfUsers() == 0 &&
                gamePhase == GamePhase.PLAY) {
            log.info("Game stopped because no users");
            gamePhase = GamePhase.WAIT;
        }
    }
}

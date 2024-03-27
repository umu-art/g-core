package ru.kazenin.gcore.services;

import ru.kazenin.model.GamePhase;

public interface PhaseService {
    GamePhase getGame();

    void startGame();

    void stopGame();

    void directStopGame();
}

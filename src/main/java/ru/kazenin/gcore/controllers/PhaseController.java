package ru.kazenin.gcore.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import ru.kazenin.api.PhaseApi;
import ru.kazenin.gcore.services.PhaseService;
import ru.kazenin.model.GamePhase;

@Controller
@RequiredArgsConstructor
public class PhaseController implements PhaseApi {
    private final PhaseService phaseService;

    @Override
    public ResponseEntity<GamePhase> getGame() {
        return ResponseEntity.ok(phaseService.getGame());
    }

    @Override
    public ResponseEntity<Void> startGame() {
        phaseService.startGame();
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> stopGame() {
        phaseService.stopGame();
        return ResponseEntity.ok().build();
    }
}

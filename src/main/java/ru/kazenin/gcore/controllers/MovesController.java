package ru.kazenin.gcore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import ru.kazenin.api.MovesApi;
import ru.kazenin.gcore.services.MovesService;
import ru.kazenin.model.Move;

@Controller
@RequiredArgsConstructor
public class MovesController implements MovesApi {
    private final MovesService movesService;

    @Override
    public ResponseEntity<Void> move(@Valid Move move) {
        movesService.move(move);
        return ResponseEntity.ok().build();
    }
}

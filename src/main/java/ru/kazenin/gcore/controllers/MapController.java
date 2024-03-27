package ru.kazenin.gcore.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import ru.kazenin.api.MapApi;
import ru.kazenin.gcore.services.MapService;
import ru.kazenin.model.GameObject;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MapController implements MapApi {
    private final MapService mapService;

    @Override
    public ResponseEntity<List<List<GameObject>>> getMap() {
        return ResponseEntity.ok(mapService.getMap());
    }
}

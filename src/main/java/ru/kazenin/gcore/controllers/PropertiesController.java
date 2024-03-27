package ru.kazenin.gcore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import ru.kazenin.api.PropertiesApi;
import ru.kazenin.gcore.services.PropertiesService;
import ru.kazenin.model.GameProperties;

@Controller
@RequiredArgsConstructor
public class PropertiesController implements PropertiesApi {
    private final PropertiesService propertiesService;

    @Override
    public ResponseEntity<GameProperties> getProperties() {
        return ResponseEntity.ok(propertiesService.getProperties());
    }

    @Override
    public ResponseEntity<Void> setProperties(@Valid GameProperties gameProperties) {
        propertiesService.setProperties(gameProperties);
        return ResponseEntity.ok().build();
    }
}

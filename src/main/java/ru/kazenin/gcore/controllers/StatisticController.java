package ru.kazenin.gcore.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import ru.kazenin.api.StatisticApi;
import ru.kazenin.gcore.services.StatisticService;
import ru.kazenin.model.GameStats;

@Controller
@RequiredArgsConstructor
public class StatisticController implements StatisticApi {
    private final StatisticService statisticService;

    @Override
    public ResponseEntity<GameStats> getStatistic() {
        return ResponseEntity.ok(statisticService.getStatistic());
    }
}

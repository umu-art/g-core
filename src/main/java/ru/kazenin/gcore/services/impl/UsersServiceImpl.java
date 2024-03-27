package ru.kazenin.gcore.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kazenin.gcore.auth.AuthHolder;
import ru.kazenin.gcore.services.UsersService;
import ru.kazenin.model.Color;
import ru.kazenin.model.RegisterData;
import ru.kazenin.model.User;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
public class UsersServiceImpl implements UsersService {

    public static final String G_ROOT = UUID.randomUUID().toString();
    private final Map<UUID, User> users = new HashMap<>();
    private final Map<UUID, OffsetDateTime> lastActualize = new HashMap<>();
    private UUID masterToken;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int getNumberOfUsers() {
        return users.size();
    }

    @Override
    public void register(RegisterData registerData) {
        if (users.containsKey(AuthHolder.token.get())) {
            throw new IllegalStateException("User already registered");
        }

        if (users.values().stream().anyMatch(it -> it.getName().equals(registerData.getName()))) {
            throw new IllegalStateException("User with this name already exists");
        }

        if (Objects.equals(registerData.getName(), "")) {
            throw new IllegalStateException("Name can't be empty");
        }

        users.put(AuthHolder.token.get(), new User()
                .name(registerData.getName())
                .color(getUniqueColor())
                .isMaster(users.isEmpty()));

        log.info("registered user: {} {}", users.get(AuthHolder.token.get()), AuthHolder.token.get());

        if (users.size() == 1) {
            masterToken = AuthHolder.token.get();
            log.info("new master: {} {}", users.get(masterToken), masterToken);
        }
    }

    private Color getUniqueColor() {
        var colors = new ArrayList<>(List.of(Color.values()));
        Collections.shuffle(colors);
        for (var color : colors) {
            if (users.values().stream().noneMatch(it -> it.getColor().equals(color))) {
                return color;
            }
        }
        throw new IllegalStateException("No free colors");
    }

    @Override
    public void remove(RegisterData registerData) {
        var userToRemove = users.entrySet().stream()
                .filter(it -> it.getValue().getName().equals(registerData.getName()))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow();

        if (isGameAdmin(AuthHolder.token.get()) ||
                AuthHolder.token.get().equals(userToRemove)) {
            remove(userToRemove);
        }
    }

    private void remove(UUID token) {
        log.info("remove user: {} {}", users.get(token), token);

        users.remove(token);
        users.keySet().remove(token);

        if (token.equals(masterToken)) {
            if (!isEmpty(users)) {
                masterToken = users.keySet()
                        .stream().findFirst().get();
                log.info("new master: {} {}", users.get(masterToken), masterToken);

                users.get(masterToken).setIsMaster(true);
            } else {
                masterToken = null;
            }
        }
    }

    @Override
    public String getUserName() {
        return users.get(AuthHolder.token.get()).getName();
    }

    @Override
    public boolean isGameUser(UUID token) {
        return users.containsKey(token);
    }

    @Override
    public boolean isGameAdmin(UUID token) {
        return masterToken.equals(token);
    }

    @Override
    public void actualize() {
        lastActualize.put(AuthHolder.token.get(), OffsetDateTime.now());
    }

    @Scheduled(fixedDelay = 10000)
    private void killInactive() {
        var usersToRemove = users.keySet()
                .stream()
                .filter(token -> OffsetDateTime.now().minusSeconds(30).isAfter(lastActualize.get(token)))
                .peek(token -> log.info("Kill inactive: {}, {}", users.get(token), token))
                .collect(Collectors.toSet());

        usersToRemove.forEach(this::remove);
    }
}

package ru.kazenin.gcore.services;

import ru.kazenin.model.RegisterData;
import ru.kazenin.model.User;

import java.util.List;
import java.util.UUID;

public interface UsersService {
    List<User> getAllUsers();

    int getNumberOfUsers();

    void register(RegisterData registerData);

    void remove(RegisterData registerData);

    String getUserName();

    boolean isGameUser(UUID token);

    boolean isGameAdmin(UUID token);

    void actualize();
}

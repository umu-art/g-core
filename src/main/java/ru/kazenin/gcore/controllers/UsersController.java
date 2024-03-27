package ru.kazenin.gcore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import ru.kazenin.api.UsersApi;
import ru.kazenin.gcore.auth.AuthHolder;
import ru.kazenin.gcore.services.UsersService;
import ru.kazenin.model.RegisterData;
import ru.kazenin.model.User;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UsersController implements UsersApi {
    private final UsersService usersService;

    @Override
    public ResponseEntity<Void> actualize() {
        usersService.actualize();
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    @Override
    public ResponseEntity<Boolean> isAdmin() {
        return ResponseEntity.ok(usersService.isGameAdmin(AuthHolder.token.get()));
    }

    @Override
    public ResponseEntity<Void> register(@Valid RegisterData registerData) {
        usersService.register(registerData);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> remove(@Valid RegisterData registerData) {
        usersService.remove(registerData);
        return ResponseEntity.ok().build();
    }
}

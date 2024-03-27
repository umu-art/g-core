package ru.kazenin.gcore.auth;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kazenin.gcore.exceptions.ForbiddenException;
import ru.kazenin.gcore.services.UsersService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthFilter implements Filter {
    private static final List<String> FOR_ALL_PATHS = List.of("/register");
    private final UsersService usersService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        var httpRequest = (HttpServletRequest) servletRequest;

        var userToken = httpRequest.getHeader("token");

        if (userToken != null &&
                (usersService.isGameUser(UUID.fromString(userToken)) ||
                        FOR_ALL_PATHS.stream().anyMatch(it -> httpRequest.getServletPath().contains(it)))) {
            AuthHolder.token.set(UUID.fromString(userToken));
            usersService.actualize();
            filterChain.doFilter(servletRequest, servletResponse);
            AuthHolder.token.remove();
        } else {
            throw new ForbiddenException("all");
        }
    }
}

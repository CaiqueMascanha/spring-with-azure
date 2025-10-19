package io.github.caiquemascanha.fullazurewithspring.controller;

import io.github.caiquemascanha.fullazurewithspring.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository repository;


    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/me")
    public ResponseEntity<String> getUser(@AuthenticationPrincipal OidcUser principal) {
        String name = principal.getAttribute("name");

        return  ResponseEntity.ok().body("Olá " + name);
    }

    @GetMapping("/privado")
    public ResponseEntity<String> getPrivate() {
        return ResponseEntity.ok().body("Rota privada");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> getAdmin() {
        return ResponseEntity.ok().body("Rota admin");
    }
}

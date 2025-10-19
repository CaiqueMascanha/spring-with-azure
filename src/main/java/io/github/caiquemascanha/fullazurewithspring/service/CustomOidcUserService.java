package io.github.caiquemascanha.fullazurewithspring.service;

import io.github.caiquemascanha.fullazurewithspring.model.Role;
import io.github.caiquemascanha.fullazurewithspring.model.User;
import io.github.caiquemascanha.fullazurewithspring.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository repository;

    public CustomOidcUserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        // 1. Carrega o usuário padrão do provedor OIDC (Microsoft)
        OidcUser oidcUser = super.loadUser(userRequest);

        // 2. Extrai as informações (claims) do usuário
        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");

        Optional<User> userOptional = repository.findByEmail(email);

        User user = userOptional.orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setRole(Role.ROLE_USER);

            return repository.save(newUser);
        });

        // 4. Cria o conjunto de 'authorities' (roles) a partir do nosso banco
        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));

        // 5. Retorna um novo OidcUser com as nossas roles customizadas
        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

    }
}

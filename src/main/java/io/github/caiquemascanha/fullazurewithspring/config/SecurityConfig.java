package io.github.caiquemascanha.fullazurewithspring.config;

import io.github.caiquemascanha.fullazurewithspring.model.Role;
import io.github.caiquemascanha.fullazurewithspring.service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOidcUserService service;

    public SecurityConfig(CustomOidcUserService service) {
        this.service = service;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desabilita o CSRF (necessário para o H2 Console)
                // CUIDADO: Em produção, você deve ter uma estratégia de CSRF mais robusta!
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorize -> authorize


                        // Rotas específicas para ADMIN
                        .requestMatchers("/api/admin/**").hasAuthority(Role.ROLE_ADMIN.name())
                        // Rotas que exigem qualquer usuário autenticado (USER ou ADMIN)
                        .requestMatchers("/api/user/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(this.service) // AQUI é onde plugamos nosso serviço customizado
                        )
                        .defaultSuccessUrl("http://localhost:3000/dashboard", true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login") // Redireciona para a home após o logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

}

package ru.blps.googleplay.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.JaasNameCallbackHandler;
import org.springframework.security.authentication.jaas.JaasPasswordCallbackHandler;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.authentication.jaas.JaasAuthenticationCallbackHandler;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.blps.googleplay.config.AppSecurityProperties;
import ru.blps.googleplay.security.jaas.PrincipalAuthorityGranter;
import ru.blps.googleplay.security.jaas.XmlJaasLoginModule;
import ru.blps.googleplay.security.jwt.JwtAuthenticationFilter;
import ru.blps.googleplay.security.jwt.JwtService;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.List;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtService jwtService(AppSecurityProperties properties) {
        return new JwtService(properties);
    }

    @Bean
    public DefaultJaasAuthenticationProvider jaasAuthenticationProvider(AppSecurityProperties properties) {
        Map<String, Object> options = Map.of(
            "usersXml", properties.getUsersXml()
        );
        AppConfigurationEntry entry = new AppConfigurationEntry(
            XmlJaasLoginModule.class.getName(),
            AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
            options
        );

        DefaultJaasAuthenticationProvider provider = new DefaultJaasAuthenticationProvider();
        provider.setConfiguration(new InMemoryConfiguration(Map.of("SPRINGSECURITY", new AppConfigurationEntry[]{entry})));
        provider.setAuthorityGranters(new AuthorityGranter[]{new PrincipalAuthorityGranter()});
        provider.setCallbackHandlers(new JaasAuthenticationCallbackHandler[]{new JaasNameCallbackHandler(), new JaasPasswordCallbackHandler()});
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DefaultJaasAuthenticationProvider jaasAuthenticationProvider) {
        return new ProviderManager(jaasAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

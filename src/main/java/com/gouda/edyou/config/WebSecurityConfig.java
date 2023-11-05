package com.gouda.edyou.config;

import com.gouda.edyou.entity.OAuth2UserImpl;
import com.gouda.edyou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
    private final UserService userService;

    @Autowired
    public WebSecurityConfig(UserService userService, OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) {
        this.userService = userService;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2UserImpl oAuth2User = (OAuth2UserImpl) authentication.getPrincipal();
            userService.processOAuthPostLogin(oAuth2User.getEmail());
            response.sendRedirect("/");
        };
    }

    private String[] resources() {
        return new String[]{"/webjars/**", "/static/js/**","/error/**"
                , "/css/**","/fonts/**","/libs/**","/img/**","/h2-console/**"};
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                                .requestMatchers(antMatcher("/**")).permitAll()
                        //.anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                        .successHandler(authenticationSuccessHandler())
                )
                .logout((logout) -> logout
                        .permitAll()
                        .logoutUrl("/logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(false)
                );

        return http.build();
    }
}

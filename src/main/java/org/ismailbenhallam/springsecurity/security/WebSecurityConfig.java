package org.ismailbenhallam.springsecurity.security;

import lombok.RequiredArgsConstructor;
import org.ismailbenhallam.springsecurity.filters.MyAuthorizationFilter;
import org.ismailbenhallam.springsecurity.filters.MyAuthenticationFilter;
import org.ismailbenhallam.springsecurity.models.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.ismailbenhallam.springsecurity.apis.RefreshTokenController.REFRESH_TOKEN_PATH;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String LOGIN_URL = "/login";
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final MyAuthenticationFilter authenticationFilter;
    private final MyAuthorizationFilter myAuthorizationFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.eraseCredentials(true).userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        authenticationFilter.setFilterProcessesUrl(LOGIN_URL);

        http
                .csrf().disable()
                .httpBasic().disable()
                .logout().disable()
                .rememberMe().disable()
//                .anonymous().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()
                .antMatchers("/signup", "/ping", REFRESH_TOKEN_PATH).permitAll()
                .antMatchers(HttpMethod.POST, LOGIN_URL + "/**").permitAll()
                .antMatchers("/admins/**").hasRole(Role.ADMIN.name())
                .anyRequest().authenticated().and()

                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(myAuthorizationFilter, MyAuthenticationFilter.class)

        //TODO: JWT
//                .oauth2ResourceServer().jwt()

//                .exceptionHandling()
//                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
//                .accessDeniedPage("/accessDeniedPage.html").and()
        ;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

}

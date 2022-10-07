package cinema.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String ADMIN_ROLE_NAME = "ADMIN";
    private static final String USER_ROLE_NAME = "USER";
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/register").permitAll()
                .antMatchers(HttpMethod.GET, "/cinema-halls",
                        "/movies", "/movie-sessions/available")
                .hasAnyRole(USER_ROLE_NAME, ADMIN_ROLE_NAME)
                .antMatchers(HttpMethod.POST, "/cinema-halls",
                        "/movies", "/movie-sessions").hasRole(ADMIN_ROLE_NAME)
                .antMatchers(HttpMethod.PUT, "/movie-sessions").hasRole(ADMIN_ROLE_NAME)
                .antMatchers(HttpMethod.POST, "/orders/complete").hasRole(USER_ROLE_NAME)
                .antMatchers(HttpMethod.GET, "/orders",
                        "/shopping-carts/by-user").hasRole(USER_ROLE_NAME)
                .antMatchers(HttpMethod.PUT, "/shopping-carts/movies-sessions")
                .hasRole(USER_ROLE_NAME)
                .antMatchers(HttpMethod.GET, "/users/by-email").hasRole(USER_ROLE_NAME)
                .antMatchers(HttpMethod.DELETE, "/movie-sessions").hasRole(ADMIN_ROLE_NAME)
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }
}

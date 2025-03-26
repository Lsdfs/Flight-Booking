package SE2.flightBooking.security;


import SE2.flightBooking.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityImpl {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenProvider(AuthService authService) {
        DaoAuthenticationProvider daoAuthenProvider = new DaoAuthenticationProvider();
        daoAuthenProvider.setUserDetailsService(authService);
        daoAuthenProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenProvider;
    }


    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(
                        configurer -> configurer
                                .requestMatchers("/", "/auth/login/**", "/auth/register/**").permitAll()
                                .anyRequest().permitAll() // or authenticated() when ready
                )
                .formLogin(
                        form -> form.loginPage("/auth/login")
                                .defaultSuccessUrl("/", true)
                                .usernameParameter("phoneNumber")
                                .passwordParameter("password")
                                .permitAll()
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/auth/login?logout")
                                .invalidateHttpSession(true) // Hủy session
                                .deleteCookies("JSESSIONID")
                                .permitAll()
                );

        return http.build();
    }
}

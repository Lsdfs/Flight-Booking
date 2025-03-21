package SE2.flightBooking.security;


import SE2.flightBooking.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityImpl {


    @Bean
    public DaoAuthenticationProvider authenProvider(AuthService authService) {
        DaoAuthenticationProvider daoAuthenProvider = new DaoAuthenticationProvider();
        daoAuthenProvider.setUserDetailsService(authService);
        return daoAuthenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(
                        configurer -> configurer
                                .requestMatchers("/", "/auth/login/**", "/auth/register/**").permitAll()
//                                .anyRequest().authenticated()  /// Tôi sẽ config phân quyền sau khi hoàn thiện url
                                .anyRequest().permitAll()
                )
                .formLogin(
                        form -> form.loginPage("/auth/login")
                                .defaultSuccessUrl("/main", true)
                                .usernameParameter("phoneNumber")
                                .permitAll()
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/logout")
                                .permitAll()
                );

        return http.build();
    }

}

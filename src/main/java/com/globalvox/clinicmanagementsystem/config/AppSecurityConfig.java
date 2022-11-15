package com.globalvox.clinicmanagementsystem.config;

import com.globalvox.clinicmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    // add a reference to our user service
    @Autowired
    private UserService userService;

    @Autowired
    private CustomSuccessHandler customSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(
                        "/error**",
                        "/api**",
                        "/verify**",
                        "/register**",
                        "/display/image/**",
                        "/js/**",
                        "/css/**",
                        "/img/**",
                        "/api/**",
                        "/webjars/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/forgot-password").permitAll()
                .antMatchers("/reset-password").permitAll()
                .antMatchers("/doctor/**").hasRole("DOCTOR")
                .antMatchers("/patient/**").hasRole("PATIENT")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/store/**").hasRole("STORE")
                .antMatchers("/staff/**").hasRole("ADMIN")
                .antMatchers("/holiday/**").hasRole("ADMIN")
                .antMatchers("/appointment/**").hasAnyRole("DOCTOR", "PATIENT")
                .antMatchers("/symptoms/**").hasAnyRole("DOCTOR", "PATIENT")
                .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .loginProcessingUrl("/authenticateUser")
//                    .successForwardUrl("/")
                    .successHandler(customSuccessHandler)
                    .permitAll()
                .and()
                    .logout()
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
                .and()
                    .rememberMe()
                    .key("test")
                    .userDetailsService(userService)
                    .tokenValiditySeconds(7 * 24 * 60 * 60)
                .and()
                    .exceptionHandling().accessDeniedPage("/access-denied").and().csrf().disable();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
}

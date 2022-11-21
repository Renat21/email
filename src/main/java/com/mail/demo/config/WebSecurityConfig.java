package com.mail.demo.config;

import com.mail.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



/**
 * Class WebSecurityConfig - класс конфигурации WebSecurity
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Lazy
    @Autowired
    private UserService userService;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeHttpRequests().mvcMatchers("/login", "/registration", "/index").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().
                loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
                .and().logout().permitAll().logoutSuccessUrl("/login").and().userDetailsService(userService);
    }

    @Override
    public void configure(WebSecurity webSecurity){
        webSecurity.ignoring().antMatchers("/resources/**/**",
                "/**/**/**.css", "/**/**.js", "/**/**.png");
    }

}
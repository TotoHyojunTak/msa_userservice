package com.ecom.user.security;

import com.ecom.user.Service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // CASE1

        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/users/**").permitAll()
                .and()
                .addFilter(getAuthenticationFilter());
        http.headers().frameOptions().disable();


        // CASE 2
        /*
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress("127.0.0.1")
                .and()
                .addFilter(getAuthenticationFilter());
        http.headers().frameOptions().disable();
        */
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception{
        return new AuthenticationFilter(authenticationManager(), userService, env);
    }

    // SELECT PWD FROM TB_USERS WHERE email=?
    // db_pwd = encrypt_input_pwd
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}

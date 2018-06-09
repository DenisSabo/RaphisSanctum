package vv.assignment.restful.user.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vv.assignment.restful.user.CustomUserDetailsService;

/**
 * Here the authentication itself takes place
 */

@ComponentScan
@Configuration
@EnableAutoConfiguration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
    @Autowired
    CustomUserDetailsService custom;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        /**
         * use our CustomUserDetailsService to authenticate user + password encoder
          */
        auth.userDetailsService(custom).passwordEncoder(passwordEncoder());

    }
}


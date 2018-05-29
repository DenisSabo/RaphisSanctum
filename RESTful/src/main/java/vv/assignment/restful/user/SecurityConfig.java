package vv.assignment.restful.user;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Configures the security conditions for paths
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // For testing reasons it is allowed to get all users without persmission
        http.authorizeRequests().antMatchers("/users").permitAll();
        http.authorizeRequests().antMatchers("/user").permitAll();
        // protect all resources
        http.authorizeRequests().anyRequest().authenticated();
        // protect with http basic authentication
        http.httpBasic();
        http.csrf().disable();
    }
}

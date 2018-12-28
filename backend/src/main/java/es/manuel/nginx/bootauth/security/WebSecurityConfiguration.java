package es.manuel.nginx.bootauth.security;

import es.manuel.nginx.bootauth.BootAuthProperties;
import es.manuel.nginx.bootauth.BootAuthProperties.SecurityProperties;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration.LiquibaseConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

import static es.manuel.nginx.bootauth.model.GrantedAuthority.Authority.ROLE_ADMIN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@EnableWebSecurity
@Configuration
@AutoConfigureAfter(LiquibaseConfiguration.class)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_URL = "/login";

    private final BootAuthProperties bootAuthProperties;
    private final DataSource dataSource;
    private final SpringLiquibase springLiquibase;

    public WebSecurityConfiguration(final BootAuthProperties bootAuthProperties, final DataSource dataSource, final SpringLiquibase springLiquibase) {
        this.bootAuthProperties = bootAuthProperties;
        this.dataSource = dataSource;
        this.springLiquibase = springLiquibase;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = passwordEncoder();
        SecurityProperties securityProperties = this.bootAuthProperties.getSecurity();
        // @formatter:off
        auth.jdbcAuthentication()
            .dataSource(this.dataSource)
            .withUser(securityProperties.getAdminUsername()).password(passwordEncoder.encode(securityProperties.getAdminPassword())).authorities(ROLE_ADMIN.name());
        // @formatter:on
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        // @formatter:off
        http.authorizeRequests()
                .antMatchers("/validate", "/login/**", "/css/**", "/js/**", "/img/**").permitAll()
                .anyRequest().hasAuthority(ROLE_ADMIN.name())
            .and().formLogin()
                .loginPage(LOGIN_URL)
                .loginProcessingUrl(LOGIN_URL)
                .failureHandler(new JWTAuthenticationFailureHandler(this.bootAuthProperties))
                .successHandler(new JWTAuthenticationSuccessHandler(this.bootAuthProperties))
                .permitAll()
            .and().logout()
                .logoutSuccessHandler(new JWTLogoutSuccessHandler("/login", this.bootAuthProperties))
            .and().csrf()
                .disable()
            .addFilterBefore(new JWTAuthenticationFilter(this.bootAuthProperties, userDetailsService()), BasicAuthenticationFilter.class)
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // @formatter:on
    }
}

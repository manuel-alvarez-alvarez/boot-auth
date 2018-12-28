package es.manuel.nginx.bootauth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import es.manuel.nginx.bootauth.BootAuthProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private final BootAuthProperties bootAuthProperties;
    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(final BootAuthProperties bootAuthProperties, final UserDetailsService userDetailsService) {
        this.bootAuthProperties = bootAuthProperties;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken token = getAuthentication(request);
        if (token != null) {
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(final HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, this.bootAuthProperties.getCookie().getName());
        if (cookie == null) {
            return null;
        }
        try {
            String username = JWT.require(HMAC512(this.bootAuthProperties.getJwt().getSecret()))
                    .build()
                    .verify(cookie.getValue())
                    .getSubject();
            if (username == null) {
                return null;
            }
            UserDetails user = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (JWTDecodeException e) {
            LOGGER.error(String.format("Error decoding the JWT token %s", cookie.getValue()), e);
            return null;
        } catch (TokenExpiredException e) {
            LOGGER.warn("The token has expired", e);
            return null;
        } catch (UsernameNotFoundException e) {
            LOGGER.warn("The user is gone", e);
            return null;
        }
    }
}

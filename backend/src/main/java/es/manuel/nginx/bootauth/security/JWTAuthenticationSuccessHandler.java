package es.manuel.nginx.bootauth.security;

import com.auth0.jwt.JWT;
import es.manuel.nginx.bootauth.BootAuthProperties;
import es.manuel.nginx.bootauth.BootAuthProperties.CookieProperties;
import es.manuel.nginx.bootauth.web.ResponseUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JWTAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final BootAuthProperties bootAuthProperties;

    public JWTAuthenticationSuccessHandler(final BootAuthProperties bootAuthProperties) {
        this.bootAuthProperties = bootAuthProperties;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        CookieProperties cookieProperties = this.bootAuthProperties.getCookie();
        Cookie cookie = new Cookie(cookieProperties.getName(), getJWTToken(authentication));
        cookie.setMaxAge(cookieProperties.getMaxAge());
        cookie.setHttpOnly(true);
        cookie.setPath(cookieProperties.getPath());
        response.addCookie(cookie);
        ResponseUtils.renderJson("{\"success\": true}", response);
    }

    private String getJWTToken(final Authentication auth) {
        User user = (User) auth.getPrincipal();
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + this.bootAuthProperties.getJwt().getExpirationTime()))
                .sign(HMAC512(this.bootAuthProperties.getJwt().getSecret()));
    }
}

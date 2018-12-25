package es.manuel.nginx.bootauth.security;

import es.manuel.nginx.bootauth.BootAuthProperties;
import es.manuel.nginx.bootauth.BootAuthProperties.CookieProperties;
import es.manuel.nginx.bootauth.web.ResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final BootAuthProperties bootAuthProperties;

    public JWTAuthenticationFailureHandler(final BootAuthProperties bootAuthProperties) {
        this.bootAuthProperties = bootAuthProperties;
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException e) throws IOException, ServletException {
        CookieProperties cookieProperties = this.bootAuthProperties.getCookie();
        Cookie cookie = new Cookie(cookieProperties.getName(), "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath(cookieProperties.getPath());
        response.addCookie(cookie);
        ResponseUtils.renderJson("{\"success\": false}", response);
    }
}

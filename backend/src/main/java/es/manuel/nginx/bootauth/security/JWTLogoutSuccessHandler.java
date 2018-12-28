package es.manuel.nginx.bootauth.security;

import es.manuel.nginx.bootauth.BootAuthProperties;
import es.manuel.nginx.bootauth.BootAuthProperties.CookieProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final BootAuthProperties bootAuthProperties;

    public JWTLogoutSuccessHandler(final String logoutUrl, final BootAuthProperties bootAuthProperties) {
        this.bootAuthProperties = bootAuthProperties;
        this.setDefaultTargetUrl(logoutUrl);
    }

    @Override
    public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        this.clearCookie(response);
        super.onLogoutSuccess(request, response, authentication);
    }

    private void clearCookie(final HttpServletResponse response) {
        CookieProperties cookieProperties = this.bootAuthProperties.getCookie();
        Cookie cookie = new Cookie(cookieProperties.getName(), "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath(cookieProperties.getPath());
        response.addCookie(cookie);
    }
}

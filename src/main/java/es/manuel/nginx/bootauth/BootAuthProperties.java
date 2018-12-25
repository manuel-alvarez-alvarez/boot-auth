package es.manuel.nginx.bootauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "boot-auth")
@Data
public class BootAuthProperties {

    private CookieProperties cookie = new CookieProperties();
    private JWTProperties jwt = new JWTProperties();
    private SecurityProperties security = new SecurityProperties();

    @Data
    public static class SecurityProperties {
        private String adminUsername;
        private String adminPassword;
    }

    @Data
    public static class JWTProperties {
        private String secret;
        private int expirationTime;
    }

    @Data
    public static class CookieProperties {
        private String name;
        private int maxAge;
        private String path;
    }
}

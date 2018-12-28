package es.manuel.nginx.bootauth.web;

import es.manuel.nginx.bootauth.exception.AuthException;
import es.manuel.nginx.bootauth.model.User;
import es.manuel.nginx.bootauth.service.UserService;
import es.manuel.nginx.bootauth.web.validator.HasPassword;
import es.manuel.nginx.bootauth.web.validator.PasswordsEqualConstraint;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static es.manuel.nginx.bootauth.model.GrantedAuthority.Authority.ROLE_USER;
import static java.util.stream.Collectors.*;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.ok;

@Controller
public class AuthController {

    private static Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;

    public AuthController(final UserService userService, final MessageSource messageSource, final PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping(value = "/validate", produces = MediaType.TEXT_HTML_VALUE)
    public void index(final HttpServletResponse response) throws IOException {
        if (userService.isAuthenticated()) {
            response.flushBuffer();
        } else {
            response.sendError(SC_UNAUTHORIZED);
        }
    }

    @GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    public String login(@RequestParam(defaultValue = "/auth") final String url, final Model model) {
        if (userService.isAuthenticated()) {
            return "redirect:/";
        } else {
            model.addAttribute("url", url);
            return "login";
        }
    }

    @GetMapping(value = "/admin/user/new", produces = MediaType.TEXT_HTML_VALUE)
    public String newUser(final Model model) {
        model.addAttribute("user", new User("", "", ROLE_USER));
        return "user/user";
    }

    @GetMapping(value = "/admin/user/{username}", produces = MediaType.TEXT_HTML_VALUE)
    public String editUser(@PathVariable final String username, final Model model) {
        model.addAttribute("user", userService.findOne(username));
        return "user/user";
    }


    @GetMapping(value = "/admin/user", produces = MediaType.TEXT_HTML_VALUE)
    public String listUsers(final Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    @PostMapping(value = "/admin/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Result> addUser(@Valid @ModelAttribute final UserRequest user, final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ok(Result.builder()
                    .errors(bindingResult.getGlobalErrors().stream().map(this::mapError).collect(toList()))
                    .fieldErrors(bindingResult.getFieldErrors().stream().collect(groupingBy(FieldError::getField, mapping(this::mapError, toList()))))
                    .build());
        } else {
            userService.save(new User(user.getUsername(), passwordEncoder.encode(user.getPassword()), ROLE_USER));
            return ok(Result.builder().redirect("/auth/admin/user").build());
        }
    }

    @PutMapping(value = "/admin/user/{username}/disable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Result> disable(@PathVariable final String username) {
        userService.disable(username);
        return ok(Result.builder().redirect("/auth/admin/user").build());
    }

    @PutMapping(value = "/admin/user/{username}/enable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Result> enable(@PathVariable final String username) {
        userService.enable(username);
        return ok(Result.builder().redirect("/auth/admin/user").build());
    }

    @DeleteMapping(value = "/admin/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Result> deleteUser(@PathVariable final String username) {
        userService.delete(username);
        return ok(Result.builder().redirect("/auth/admin/user").build());
    }

    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<Result> onException(final Throwable e) {
        LOGGER.error("Error in the AuthController class", e);
        if (AuthException.class.isInstance(e)) {
            return ok(Result.builder()
                    .errors(Collections.singletonList(messageSource.getMessage(AuthException.class.cast(e), Locale.getDefault())))
                    .build());
        } else {
            return ok(Result.builder()
                    .errors(Collections.singletonList(e.getMessage()))
                    .build());
        }
    }

    private String mapError(final ObjectError error) {
        return messageSource.getMessage(error, Locale.getDefault());
    }

    @Data
    @PasswordsEqualConstraint(message = "Passwords are not equal")
    public static class UserRequest implements HasPassword {
        @Size(min = 3, max = 255)
        private String username;
        @Size(min = 3, max = 255)
        private String password;
        @Size(min = 3, max = 255)
        private String passwordRepeat;
    }

    @Data
    @Builder
    public static class Result {
        private List<String> errors;
        private Map<String, List<String>> fieldErrors;
        private String redirect;
    }
}

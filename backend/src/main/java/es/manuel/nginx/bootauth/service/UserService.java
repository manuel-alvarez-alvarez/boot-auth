package es.manuel.nginx.bootauth.service;

import es.manuel.nginx.bootauth.exception.AuthException;
import es.manuel.nginx.bootauth.exception.UserAlreadyExistsException;
import es.manuel.nginx.bootauth.exception.UserNotFoundException;
import es.manuel.nginx.bootauth.model.GrantedAuthority;
import es.manuel.nginx.bootauth.model.User;
import es.manuel.nginx.bootauth.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static es.manuel.nginx.bootauth.model.GrantedAuthority.Authority.ROLE_ANONYMOUS;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAuthenticated() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .noneMatch(it -> it.getAuthority().equals(ROLE_ANONYMOUS.name()));
    }

    @Transactional
    public void save(final User user) {
        if (userRepository.findById(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public List<User> findAll() {
        return userRepository.findAll(Sort.by(Sort.Order.asc("username")));
    }

    @Transactional
    public User findOne(final String username) {
        try {
            return userRepository.getOne(username);
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException(username, e);
        }
    }

    @Transactional
    public User getCurrentUser() {
        String username = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUsername();
        return findOne(username);
    }

    @Transactional
    public void disable(final String username) {
        setEnabled(username, false);
    }

    @Transactional
    public void enable(final String username) {
        setEnabled(username, true);
    }

    @Transactional
    public void delete(final String username) {
        Optional<User> optional = userRepository.findById(username);
        if (!optional.isPresent()) {
            throw new UserNotFoundException(username);
        }
        User user = optional.get();
        userRepository.delete(user);
    }

    private void setEnabled(final String username, final boolean enabled) {
        Optional<User> optional = userRepository.findById(username);
        if (!optional.isPresent()) {
            throw new UserNotFoundException(username);
        }
        User user = optional.get();
        user.setEnabled(enabled);
        userRepository.save(user);
    }
}

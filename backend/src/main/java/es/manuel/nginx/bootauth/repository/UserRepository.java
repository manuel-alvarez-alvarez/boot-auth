package es.manuel.nginx.bootauth.repository;

import es.manuel.nginx.bootauth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}

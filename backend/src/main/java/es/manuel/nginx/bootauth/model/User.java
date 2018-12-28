package es.manuel.nginx.bootauth.model;

import es.manuel.nginx.bootauth.model.GrantedAuthority.Authority;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(of = "username")
public class User {

    @Id
    private String username;
    private String password;
    private boolean enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private Set<GrantedAuthority> authorities;

    public User() {

    }

    public User(final String username, final String password, final Authority... roles) {
        this.username = username;
        this.password = password;
        this.enabled = true;
        this.authorities = Arrays.stream(roles).map(it -> new GrantedAuthority(this, it)).collect(Collectors.toSet());
    }


}

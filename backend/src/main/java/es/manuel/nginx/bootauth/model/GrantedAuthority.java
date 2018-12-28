package es.manuel.nginx.bootauth.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "authorities")
@IdClass(GrantedAuthorityId.class)
@Data
public class GrantedAuthority {

    public enum Authority {
        ROLE_USER,
        ROLE_ADMIN,
        ROLE_ANONYMOUS
    }

    @Id
    private String username;
    @Id
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    private User user;

    public GrantedAuthority() {

    }

    public GrantedAuthority(final User user, final Authority authority) {
        this.user = user;
        this.username = user.getUsername();
        this.authority = authority;
    }

    @Override
    public String toString( ){
        return authority.name();
    }


}

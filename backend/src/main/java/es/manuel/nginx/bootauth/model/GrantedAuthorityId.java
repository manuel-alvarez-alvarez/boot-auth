package es.manuel.nginx.bootauth.model;

import es.manuel.nginx.bootauth.model.GrantedAuthority.Authority;
import lombok.Data;

import java.io.Serializable;

@Data
public class GrantedAuthorityId implements Serializable {

    private String username;
    private Authority authority;
}

package com.epam.esm;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements UserDetails {

    private User user;

    private List<AuthGroup> authGroups;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<SimpleGrantedAuthority> grantedAuthorities;
        if (Objects.nonNull(authGroups)) {
            grantedAuthorities = new HashSet<>();
            authGroups.forEach(group -> grantedAuthorities.add(new SimpleGrantedAuthority(group.getAuthGroup())));
        } else {
            grantedAuthorities = Collections.emptySet();
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.user.getSecondName();
    }

    @Override
    public String getUsername() {
        return this.user.getFirstName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

package kr.hhplus.be.server.config.security;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.config.security.jwt.JwtTokenPayload;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor(staticName = "of")
public class AuthUser implements UserDetails {

    private String id;
    private String email;
    private UserRole role;

    public static AuthUser from(User user) {
        return AuthUser.of(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    public static AuthUser from(JwtTokenPayload payload) {
        if (payload.getAuthorities() == null || payload.getAuthorities().isEmpty()) {
            throw new BusinessException(CommonErrorCode.TOKEN_ERROR);
        }

        return AuthUser.of(
                payload.getId(),
                payload.getEmail(),
                UserRole.valueOf(payload.getAuthorities().getFirst().replace("ROLE_", ""))
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}

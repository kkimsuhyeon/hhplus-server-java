package kr.hhplus.be.server.config.security.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsonwebtoken.Claims;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenPayload {

    private static final String CLAIM_ID = "id";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_AUTHORITIES = "authorities";

    private String id;
    private String email;
    private List<String> authorities;

    public static JwtTokenPayload from(User user) {
        return JwtTokenPayload.builder()
                .id(user.getId())
                .email(user.getEmail())
                .authorities(Collections.singletonList("ROLE_" + user.getRole().name()))
                .build();
    }

    @SuppressWarnings("unchecked")
    public static JwtTokenPayload from(Claims claims) {
        return JwtTokenPayload.builder()
                .id(claims.get(CLAIM_ID).toString())
                .email(claims.get(CLAIM_EMAIL).toString())
                .authorities((List<String>) claims.get(CLAIM_AUTHORITIES))
                .build();
    }

    public Map<String, Object> toMap() {
        return Map.of(CLAIM_ID, id, CLAIM_EMAIL, email, CLAIM_AUTHORITIES, authorities);
    }

    @JsonIgnore
    public List<GrantedAuthority> getGrantedAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}

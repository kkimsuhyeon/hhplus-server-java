package kr.hhplus.be.server.domain.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import kr.hhplus.be.server.shared.enums.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole implements ValueEnum {
    USER("user"),
    ADMIN("admin"),
    ;

    @JsonValue
    private final String value;

    @JsonCreator
    public static UserRole fromValue(String value) {
        return ValueEnum.fromValue(value, UserRole.class);
    }

}

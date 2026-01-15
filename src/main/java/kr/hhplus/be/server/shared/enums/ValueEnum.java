package kr.hhplus.be.server.shared.enums;

import io.micrometer.common.util.StringUtils;

public interface ValueEnum {

    String getValue();

    static <E extends Enum<E> & ValueEnum> E fromValue(String value, Class<E> type) {
        if (StringUtils.isBlank(value)) return null;
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException error) {
            for (E constant : type.getEnumConstants()) {
                if (value.equals(constant.getValue())) {
                    return constant;
                }
            }

            throw new IllegalArgumentException(String.format("값 확인 필요: type: %s, value: %s", type.getSimpleName(), value));
        }

    }
}

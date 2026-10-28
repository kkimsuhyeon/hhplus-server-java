package kr.hhplus.be.server.shared.hash;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashUtilsTest {

    private static final String SHA256_OF_TEST = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";

    @Test
    void 알려진_입력의_SHA256_값을_반환한다() {
        String actual = HashUtils.sha256("test");
        assertThat(actual).isEqualTo(SHA256_OF_TEST);
    }

    @Test
    void 숫자가_같아도_문자열이_다르면_해시가_다르다() {
        String actual1 = HashUtils.sha256("100");
        String actual2 = HashUtils.sha256("100.00");

        assertThat(actual1).isNotEqualTo(actual2);
    }

    @Test
    void null이면_NPE() {
        assertThatThrownBy(() -> HashUtils.sha256(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value");
    }
}
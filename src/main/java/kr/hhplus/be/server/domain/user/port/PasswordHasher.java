package kr.hhplus.be.server.domain.user.port;

@FunctionalInterface
public interface PasswordHasher {

    String hash(String rawPassword);
}

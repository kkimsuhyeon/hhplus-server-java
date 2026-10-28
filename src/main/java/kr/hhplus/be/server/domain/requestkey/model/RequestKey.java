package kr.hhplus.be.server.domain.requestkey.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class RequestKey {

    private String operation;

    private String payloadHash;

    public boolean isSameRequest(RequestKey other) {
        return this.operation.equals(other.getOperation()) && this.payloadHash.equals(other.getPayloadHash());
    }

}

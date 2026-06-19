package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;

import java.math.BigDecimal;

public class PaymentFixture {

    private String id = "hash";
    private PaymentStatus status = PaymentStatus.PENDING;
    private BigDecimal amount = BigDecimal.ZERO;
    private String reservationId = "hash";
    private String rmk = null;

    public static PaymentFixture aPayment() {
        return new PaymentFixture();
    }

    public PaymentFixture id(String v) {
        this.id = v;
        return this;
    }

    public PaymentFixture status(PaymentStatus v) {
        this.status = v;
        return this;
    }

    public PaymentFixture amount(BigDecimal v) {
        this.amount = v;
        return this;
    }

    public PaymentFixture reservationId(String v) {
        this.reservationId = v;
        return this;
    }

    public PaymentFixture rmk(String v) {
        this.rmk = v;
        return this;
    }

    public Payment build() {
        return Payment.of(id, status, amount, reservationId, rmk);
    }
}

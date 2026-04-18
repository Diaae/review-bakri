package it.schwarz.jobs.review.coupon.provider.jpa;

import java.math.BigDecimal;

public interface CouponWithApplicationCount {
    String getCode();
    BigDecimal getDiscount();
    BigDecimal getMinBasketValue();
    String getDescription();
    long getApplicationCount();
}

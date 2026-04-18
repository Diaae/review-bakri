package it.schwarz.jobs.review.coupon.service;

import it.schwarz.jobs.review.coupon.domain.entity.ApplicationResult;
import it.schwarz.jobs.review.coupon.domain.entity.Basket;
import it.schwarz.jobs.review.coupon.domain.entity.Coupon;
import it.schwarz.jobs.review.coupon.domain.entity.CouponApplications;
import it.schwarz.jobs.review.coupon.domain.usecase.CouponUseCases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Goal of this service is to avoid coupling Transactions logic with the domain logic and to have a better separation of concerns.
 * This service is responsible for handling the application of coupons to baskets.
 * It acts as a bridge between the API layer and the domain use cases, ensuring that
 * the business logic for applying coupons is properly executed and the architecture stays purely hexagonal and clean.
 */

@Service
public class CouponApplicationService {
    private final CouponUseCases couponUseCases;

    public CouponApplicationService(CouponUseCases couponUseCases) {
        this.couponUseCases = couponUseCases;
    }

    @Transactional
    public ApplicationResult applyCoupon(Basket basket, String couponCode) {
        return couponUseCases.applyCoupon(basket, couponCode);
    }

    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        return couponUseCases.createCoupon(coupon);
    }

    @Transactional(readOnly = true)
    public List<Coupon> findAllCoupons() {
        return couponUseCases.findAllCoupons();
    }

    @Transactional(readOnly = true)
    public CouponApplications getApplications(String couponCode) {
        return couponUseCases.getApplications(couponCode);
    }
}

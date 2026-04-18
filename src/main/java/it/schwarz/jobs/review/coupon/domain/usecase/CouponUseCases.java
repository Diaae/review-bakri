package it.schwarz.jobs.review.coupon.domain.usecase;

import it.schwarz.jobs.review.coupon.domain.entity.ApplicationResult;
import it.schwarz.jobs.review.coupon.domain.entity.Basket;
import it.schwarz.jobs.review.coupon.domain.entity.Coupon;
import it.schwarz.jobs.review.coupon.domain.entity.CouponApplications;

import java.util.List;

/**
 * Whilst @Bean in CouponAppConfig is responsible for wiring the dependencies together,
 * and @Transactional will be still possible to use via a Spring proxy fron the configCoupon Class,
 * It is better to move the Transactions logic to a proper Service layer.
 */
public class CouponUseCases {

    private final CouponProvider couponProvider;

    public CouponUseCases(CouponProvider couponProvider) {
        this.couponProvider = couponProvider;
    }

    public Coupon createCoupon(Coupon coupon) {
            return couponProvider.createCoupon(coupon);
    }

    public List<Coupon> findAllCoupons() {
        return couponProvider.findAll();
    }

    public CouponApplications getApplications(String couponCode) {
        var foundCouponApplications = couponProvider.getCouponApplications(couponCode);
        if (foundCouponApplications.isEmpty()) {
            throw new CouponCodeNotFoundException("Coupon-Code " + couponCode + " not found.");
        }
        return foundCouponApplications.get();
    }

    public ApplicationResult applyCoupon(Basket basket, String couponCode) {

        var couponToApply = couponProvider.findById(couponCode)
                .orElseThrow(() -> new CouponCodeNotFoundException(("Coupon-Code " + couponCode + " not found.")));

        //Delegate business logic to the rich domain model entitiy Coupon.
        ApplicationResult result = couponToApply.applyTo(basket);


        // Register the usage of this coupon
        couponProvider.registerCouponApplication(couponToApply.getCode());

        // Apply
        return new ApplicationResult(basket, couponToApply);
    }

}

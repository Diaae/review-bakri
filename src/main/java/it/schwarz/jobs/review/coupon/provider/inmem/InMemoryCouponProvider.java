package it.schwarz.jobs.review.coupon.provider.inmem;

import it.schwarz.jobs.review.coupon.domain.entity.AmountOfMoney;
import it.schwarz.jobs.review.coupon.domain.entity.Coupon;
import it.schwarz.jobs.review.coupon.domain.entity.CouponApplications;
import it.schwarz.jobs.review.coupon.domain.usecase.CouponProvider;

import java.time.Instant;
import java.util.*;

/**
 * InMemory Implementation to simplify local test and development.
 * You can use this if there is no real database available.
 */
public class InMemoryCouponProvider implements CouponProvider {

    private final Set<Coupon> coupons = new HashSet<>();
    private final Set<CouponApplications> couponApplications = new HashSet<>();

    public InMemoryCouponProvider() {
        // Test Coupons
        coupons.add(new Coupon("TEST_05_50", AmountOfMoney.of("5.00"), AmountOfMoney.of("50.00"), "5 for 50"));
        coupons.add(new Coupon("TEST_15_100", AmountOfMoney.of("15.00"), AmountOfMoney.of("100.00"), "15 for 100"));
        coupons.add(new Coupon("TEST_40_200", AmountOfMoney.of("40.00"), AmountOfMoney.of("200.00"), "40  for 200"));


        // Test DateTimes
        var applicationDateTimes = new ArrayList<Instant>();
        applicationDateTimes.add(Instant.now().plusSeconds(1));
        applicationDateTimes.add(Instant.now().plusSeconds(2));
        applicationDateTimes.add(Instant.now().plusSeconds(3));
        applicationDateTimes.add(Instant.now().plusSeconds(4));
        couponApplications.add(
                new CouponApplications("TEST_05_50", applicationDateTimes)
        );
    }


    @Override
    public Coupon createCoupon(Coupon coupon) {
        // Coupon must not already exist
        var foundCoupon = this.findById(coupon.getCode());
        if (foundCoupon.isPresent()) {
            throw new IllegalStateException("Coupon already exists: " + coupon.getCode());
        }

        coupons.add(coupon);
        return coupon;
    }

    @Override
    public List<Coupon> findAll() {
        return coupons.stream().toList();
    }

    @Override
    public Optional<Coupon> findById(String couponCode) {
        return coupons.stream()
                .filter(it -> it.getCode().equals(couponCode))
                .findFirst();
    }

    @Override
    public void registerCouponApplication(String couponCode) {
        // Intentionally left blank, because it is currently not used
        //TODO: Implement this method to avoid future bugs when using the in-memory provider for testing.
        throw new UnsupportedOperationException("Operation not supported yet.");
    }

    @Override
    public Optional<CouponApplications> getCouponApplications(String couponCode) {
        return couponApplications.stream()
                .filter(it -> it.getCouponCode().equals(couponCode))
                .findFirst();
    }
}

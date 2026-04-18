package it.schwarz.jobs.review.coupon.provider.jpa;

import it.schwarz.jobs.review.coupon.domain.entity.AmountOfMoney;
import it.schwarz.jobs.review.coupon.domain.entity.Coupon;
import it.schwarz.jobs.review.coupon.domain.entity.CouponApplications;
import it.schwarz.jobs.review.coupon.domain.usecase.CouponAlreadyExistsException;
import it.schwarz.jobs.review.coupon.domain.usecase.CouponProvider;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Since Low level module JpaCouponProvider is created manually and is not @Bean, @Transactional won't work properly.
 * Move @Transactional our from here and manage transactions in the service layer
 * to have a better separation of concerns and to ensure that transactions are properly handled.
 **/
public class JpaCouponProvider implements CouponProvider {

    private final CouponJpaRepository couponJpaRepository;
    private final ApplicationJpaRepository applicationRepository;

    public JpaCouponProvider(CouponJpaRepository couponJpaRepository, ApplicationJpaRepository applicationRepository) {
        this.couponJpaRepository = couponJpaRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public Coupon createCoupon(Coupon coupon) {
        if (couponJpaRepository.existsById(coupon.getCode())) {
            throw new CouponAlreadyExistsException("Coupon already exists: " + coupon.getCode());
        }
        var toPersist = domainToJpa(coupon);
        var persisted = couponJpaRepository.save(toPersist);
        return jpaToDomain(persisted);
    }

    //TODO: jpaToDomain leads to N+1 select problem, we should use a custom query to fetch the coupon with the count of applications in one go.
    @Override
    public List<Coupon> findAll() {
        return couponJpaRepository.findAllWithApplicationCount().stream()
                //Use projection to avoid loading the entire CouponJpaEntity and its applications, which would lead to N+1 select problem.
                .map(this::projectionToDomain)
                .toList();
    }

    @Override
    public void registerCouponApplication(String couponCode) {
        applicationRepository.save(new ApplicationJpaEntity(
                couponCode,
                Instant.now()));
    }

    @Override
    public Optional<Coupon> findById(String couponCode) {
        var found = couponJpaRepository.findById(couponCode);
        return found.map(this::jpaToDomain);
    }

    @Override
    public Optional<CouponApplications> getCouponApplications(String couponCode) {
        var found = couponJpaRepository.findById(couponCode);
        return found.map(couponJpaEntity -> new CouponApplications(
                couponJpaEntity.getCode(),
                couponJpaEntity.getApplications().stream()
                        .map(ApplicationJpaEntity::getTimestamp)
                        .toList()));
    }

    private CouponJpaEntity domainToJpa(Coupon coupon) {
        return new CouponJpaEntity(
                coupon.getCode(),
                coupon.getDiscount().toBigDecimal(),
                coupon.getDescription(),
                coupon.getMinBasketValue().toBigDecimal()
        );
    }

    private Coupon jpaToDomain(CouponJpaEntity couponJpaEntity) {

        return new Coupon(
                couponJpaEntity.getCode(),
                AmountOfMoney.of(couponJpaEntity.getDiscount()),
                AmountOfMoney.of(couponJpaEntity.getMinBasketValue()),
                couponJpaEntity.getDescription(),
                couponJpaEntity.getApplications() == null ? 0 : couponJpaEntity.getApplications().size()
        );
    }

    private Coupon projectionToDomain (CouponWithApplicationCount projection) {
        return new Coupon(
                projection.getCode(),
                AmountOfMoney.of(projection.getDiscount()),
                AmountOfMoney.of(projection.getMinBasketValue()),
                projection.getDescription(),
                (int) projection.getApplicationCount()
        );
    }

}

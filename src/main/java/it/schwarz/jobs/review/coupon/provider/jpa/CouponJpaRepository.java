package it.schwarz.jobs.review.coupon.provider.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, String> {

    /**
     * Avoid the N+1 problem by fetching the application count in the same query.
     * Projection to avoid loading the entire CouponJpaEntity and its applications.
     * Avoided using CouponDto directly in order not to violate clean architecture pattern. (Dependency from domain to provider layer)
     */
    @Query("SELECT c.code AS code, " +
            "c.discount AS discount, " +
            "c.minBasketValue AS minBasketValue, " +
            "c.description AS description, " +
            "COUNT(a) AS applicationCount " +
            "FROM CouponJpaEntity c LEFT JOIN c.applications a " +
            "GROUP BY c.code, c.discount, c.minBasketValue, c.description")
    List<CouponWithApplicationCount> findAllWithApplicationCount();
}

package it.schwarz.jobs.review.coupon;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import it.schwarz.jobs.review.coupon.domain.usecase.CouponProvider;
import it.schwarz.jobs.review.coupon.domain.usecase.CouponUseCases;
import it.schwarz.jobs.review.coupon.provider.inmem.InMemoryCouponProvider;
import it.schwarz.jobs.review.coupon.provider.jpa.ApplicationJpaRepository;
import it.schwarz.jobs.review.coupon.provider.jpa.CouponJpaRepository;
import it.schwarz.jobs.review.coupon.provider.jpa.JpaCouponProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class CouponAppConfig {

    /**
     * No code changes needed to switch the active profile
     * Doesn't load unnecessary beans into memory
     * Clean separation of concerns for the use of different implementations of the CouponProvider
     */

    @Bean
    @Profile("prd")
    public CouponProvider getJpaCouponProvider(
            CouponJpaRepository couponJpaRepository,
            ApplicationJpaRepository applicationRepository) {
        return new JpaCouponProvider(couponJpaRepository, applicationRepository);
    }

    // Comment in/out one of the CouponProvider Beans to select one for runtime
    @Bean
    @Profile("dev")
    public CouponProvider getInMemCouponProvider() {
        return new InMemoryCouponProvider();
    }

    @Bean
    public CouponUseCases getCouponUseCases(CouponProvider couponProvider) {
        return new CouponUseCases(couponProvider);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Schwarz Corporate Solutions Coupon Management API")
                        .version("1.0.0")
                        .description("""
                            RESTful API for managing SchwarzGruppe Coupons.
                            """
                        )
                        .contact(new Contact()
                                .name("Schwarz Corporate Solutions")
                                .url("www.corporate-solutions.schwarz"))
                        .license(new License()
                                .name("Review Project")
                                .url("https://github.com/Diaae/review-bakri")
                        )
                );
    }

}

package it.schwarz.jobs.review.coupon.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.schwarz.jobs.review.coupon.api.dto.*;
import it.schwarz.jobs.review.coupon.service.CouponApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO:  Please extract all mapping logic into a dedicated CouponApiMapper component.
// Alternatively, we could also consider using a mapping library like MapStruct to automate this process and reduce boilerplate code.
/**
 * In accordance with Clean Architecture principles,
 * our DTOs should be 'dumb' data carriers. Currently,
 * these records are coupled to the Domain layer through translation methods and static factories.
 * This ensures our API contracts remain independent of our internal domain models
 * The DTO should not know how to instantiate a Domain Entity.
 * This logic belongs in a Mapper, which will protect the Domain layer from changes in the Web API structure."
 */

@RestController
@RequestMapping(path = "/api/v1/coupons")
@Tag(name = "Coupon Management", description = "API for managing SchwarzGruppe Coupons")
public class CouponRestController {

    //private final CouponUseCases couponUseCases;
    private final CouponApplicationService applicationService;


    public CouponRestController(CouponApplicationService applicationService) {
        this.applicationService = applicationService;
    }


    @Operation(
            summary = "Get all coupons",
            description = "Retrieves a list of all coupons loaded from the selected datasource"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of coupons",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetCouponsResponseDto.class)))
    })
    @GetMapping()
    public ResponseEntity<GetCouponsResponseDto> getCoupons() {
        var coupons = applicationService.findAllCoupons();

        // Map from Domain to API
        var response = GetCouponsResponseDto.of(coupons);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new coupon",
            description = "Creates a new coupon with the provided details and returns the created coupon information. " +
                    "The request must include a unique coupon code, discount amount, minimum basket value, and description."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateCouponRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or validation error",
                    content = @Content)
    })
    @PostMapping()
    public ResponseEntity<CreateCouponResponseDto> createCoupon(@Valid @RequestBody CreateCouponRequestDto request) {

        // Map from API to Domain
        var coupon = request.toCoupon();

        var couponCreated = applicationService.createCoupon(coupon);

        // Map from Domain to API and return
        var response = CreateCouponResponseDto.of(couponCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get the usage history of a coupon",
            description = "Retrieves a list of all history with timestamps for a coupon"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the history of the coupon",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetCouponApplicationsResponseDto.class)))
    })
    @GetMapping("/{couponCode}/applications")
    public ResponseEntity<GetCouponApplicationsResponseDto> getCouponApplications(@PathVariable("couponCode") String couponCode) {
        var couponApplications = applicationService.getApplications(couponCode);

        // Map from Domain to API
        var response = GetCouponApplicationsResponseDto.of(couponApplications);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Apply a coupon to a basket",
            description = "Applies a coupon to a given basket and returns the result of the application, " +
                    "including any discounts applied and the final price after applying the coupon"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon successfully applied to the basket",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplyCouponResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or validation error",
                    content = @Content)
    })
    @PostMapping("/applications")
    public ResponseEntity<ApplyCouponResponseDto> applyCoupon(@Valid @RequestBody ApplyCouponRequestDto request) {

        // Map from API to Domain
        var basket = request.basket().toBasket();
        var couponCode = request.couponCode();

        var applicationResult = applicationService.applyCoupon(basket, couponCode);

        // Map from Domain to API and return
        var response = ApplyCouponResponseDto.of(applicationResult);
        return ResponseEntity.ok(response);
    }

}

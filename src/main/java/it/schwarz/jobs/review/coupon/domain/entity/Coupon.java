package it.schwarz.jobs.review.coupon.domain.entity;

public class Coupon {

    private final String code;
    private final AmountOfMoney discount;
    private final AmountOfMoney minBasketValue;
    private final String description;
    private final long applicationCount;

    public Coupon(String code, AmountOfMoney discount, AmountOfMoney minBasketValue, String description) {
        this(code, discount, minBasketValue, description, 0);
    }

    public Coupon(String code, AmountOfMoney discount, AmountOfMoney minBasketValue, String description, long applicationCount) {
        this.code = code;
        this.discount = discount;
        this.minBasketValue = minBasketValue;
        this.description = description;
        this.applicationCount = applicationCount;
    }

    public String getCode() {
        return code;
    }

    public AmountOfMoney getDiscount() {
        return discount;
    }

    public AmountOfMoney getMinBasketValue() {
        return minBasketValue;
    }

    public String getDescription() {
        return description;
    }

    public long getApplicationCount() {
        return applicationCount;
    }

    //Add business rules
    public boolean isApplicable(Basket basket) {
        return !basket.getValue().isLessThan(minBasketValue);
    }

    public ApplicationResult applyTo(Basket basket) {
        if (!isApplicable(basket)) {
            throw new IllegalStateException("Basket value is less than the minimum required for this coupon.");
        }

        AmountOfMoney newBasketValue = basket.getValue().subtract(discount);
        Basket discountedBasket = new Basket(newBasketValue);

        return new ApplicationResult(discountedBasket, this);
    }
}

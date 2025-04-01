package com.akedev7.pos.enum

typealias PriceMultiplierRange = ClosedRange<Double>
enum class PaymentMethod(
    val priceMultiplierRange: PriceMultiplierRange,
    val pointsMultiplier: Double
) {
    CASH(
        priceMultiplierRange = 0.9..1.0,
        pointsMultiplier = 0.05
    ),
    CASH_ON_DELIVERY(
        priceMultiplierRange = 1.0..1.0,
        pointsMultiplier = 0.05
    ),
    VISA(
        priceMultiplierRange = 0.95..1.0,
        pointsMultiplier = 0.03
    ),
    MASTERCARD(
        priceMultiplierRange = 0.95..1.0,
        pointsMultiplier = 0.03
    ),
    AMEX(
        priceMultiplierRange = 0.98..1.0,
        pointsMultiplier = 0.02
    ),
    JCB(
        priceMultiplierRange = 0.95..1.0,
        pointsMultiplier = 0.05
    ),
    LINE_PAY(
        priceMultiplierRange = 1.0..1.0,
        pointsMultiplier = 0.01
    ),
    PAYPAY(
        priceMultiplierRange = 1.0..1.0,
        pointsMultiplier = 0.01
    ),
    POINTS(
        priceMultiplierRange = 1.0..1.0,
        pointsMultiplier = 0.01
    ),
    GRAB_PAY(
        priceMultiplierRange = 1.0..1.0,
        pointsMultiplier = 0.01
    ),
    BANK_TRANSFER(
        priceMultiplierRange = 1.0..1.0,
        pointsMultiplier = 0.9
    ),
    CHEQUE(
        priceMultiplierRange = 0.9..1.0,
        pointsMultiplier = 0.01
    );

    /**
     * Calculates the final price range for a given base price
     */
    fun calculatePriceRange(basePrice: Double): ClosedRange<Double> =
        (basePrice * priceMultiplierRange.start)..(basePrice * priceMultiplierRange.endInclusive)

    /**
     * Calculates the points earned for a given base price
     */
    fun calculatePoints(basePrice: Double): Double =
        basePrice * pointsMultiplier

    /**
     * Gets the price multiplier range as a Pair
     */
    fun priceMultiplierPair(): Pair<Double, Double> =
        priceMultiplierRange.start to priceMultiplierRange.endInclusive
}
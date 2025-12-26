package com.delivery.RouteX.util;

import com.delivery.RouteX.dto.order.PriceEstimateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Pricing Calculator for delivery orders
 * Calculates total price based on distance, weight, time, and promo codes
 */
@Component
@RequiredArgsConstructor
public class PricingCalculator {

    @Value("${app.delivery.base-fare}")
    private Double baseFare;

    @Value("${app.delivery.per-km-rate}")
    private Double perKmRate;

    @Value("${app.delivery.per-kg-rate}")
    private Double perKgRate;

    @Value("${app.delivery.peak-hour-multiplier}")
    private Double peakHourMultiplier;

    @Value("${app.delivery.peak-hours}")
    private String peakHours;

    private final DistanceCalculator distanceCalculator;

    // Promo codes (in production, this should be in database)
    private final Map<String, Double> promoCodes = new HashMap<>() {{
        put("FIRST10", 0.10);  // 10% discount
        put("SAVE20", 0.20);   // 20% discount
        put("WELCOME", 0.15);  // 15% discount
        put("NEWUSER", 0.25);  // 25% discount for new users
    }};

    /**
     * Calculate complete price estimate for an order
     *
     * @param distanceKm Distance in kilometers
     * @param weightKg Package weight in kilograms
     * @param promoCode Promotional code (optional)
     * @return Complete price breakdown
     */
    public PriceEstimateResponse calculatePrice(
            double distanceKm,
            double weightKg,
            String promoCode
    ) {
        // 1. Base fare (fixed amount)
        double base = baseFare;

        // 2. Distance charge (per kilometer)
        double distanceCharge = distanceKm * perKmRate;

        // 3. Weight charge (per kilogram)
        double weightCharge = weightKg * perKgRate;

        // 4. Peak hour surcharge (if current time is in peak hours)
        double peakSurcharge = 0.0;
        if (isPeakHour()) {
            double subtotal = base + distanceCharge + weightCharge;
            peakSurcharge = subtotal * (peakHourMultiplier - 1);
        }

        // 5. Calculate subtotal before discount
        double subtotal = base + distanceCharge + weightCharge + peakSurcharge;

        // 6. Apply promo code discount
        double discount = 0.0;
        if (promoCode != null && promoCodes.containsKey(promoCode.toUpperCase())) {
            discount = subtotal * promoCodes.get(promoCode.toUpperCase());
        }

        // 7. Calculate final total
        double total = subtotal - discount;

        // 8. Calculate estimated delivery time
        int estimatedMinutes = distanceCalculator.calculateEstimatedTime(distanceKm);
        String estimatedTime = distanceCalculator.formatEstimatedTime(estimatedMinutes);

        // Return complete price breakdown
        return PriceEstimateResponse.builder()
                .distanceKm(Math.round(distanceKm * 100.0) / 100.0)
                .baseFare(Math.round(base * 100.0) / 100.0)
                .distanceCharge(Math.round(distanceCharge * 100.0) / 100.0)
                .weightCharge(Math.round(weightCharge * 100.0) / 100.0)
                .peakHourSurcharge(Math.round(peakSurcharge * 100.0) / 100.0)
                .discount(Math.round(discount * 100.0) / 100.0)
                .totalAmount(Math.round(total * 100.0) / 100.0)
                .estimatedTime(estimatedTime)
                .build();
    }

    /**
     * Check if current time is within peak hours
     * Peak hours are defined in application.yml
     * Format: "08:00-10:00,17:00-20:00"
     *
     * @return true if current time is peak hour
     */
    private boolean isPeakHour() {
        LocalTime now = LocalTime.now();

        // Parse peak hours configuration
        String[] ranges = peakHours.split(",");

        for (String range : ranges) {
            String[] times = range.trim().split("-");
            LocalTime start = LocalTime.parse(times[0]);
            LocalTime end = LocalTime.parse(times[1]);

            // Check if current time is within this range
            if (now.isAfter(start) && now.isBefore(end)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Validate if a promo code is valid
     *
     * @param promoCode Promotional code to check
     * @return true if valid
     */
    public boolean isValidPromoCode(String promoCode) {
        return promoCode != null &&
                promoCodes.containsKey(promoCode.toUpperCase());
    }

    /**
     * Get discount percentage for a promo code
     *
     * @param promoCode Promotional code
     * @return Discount as decimal (0.10 = 10%)
     */
    public Double getPromoCodeDiscount(String promoCode) {
        if (promoCode == null) return 0.0;
        return promoCodes.getOrDefault(promoCode.toUpperCase(), 0.0);
    }

    /**
     * Add a new promo code (for admin functionality)
     *
     * @param code Promo code
     * @param discountPercent Discount percentage (10 = 10%)
     */
    public void addPromoCode(String code, double discountPercent) {
        promoCodes.put(code.toUpperCase(), discountPercent / 100.0);
    }
}
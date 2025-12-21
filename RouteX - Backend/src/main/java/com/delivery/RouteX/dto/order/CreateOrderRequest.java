package com.delivery.RouteX.dto.order;

import com.delivery.RouteX.model.Order;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    @NotBlank
    private String pickupAddress;
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double pickupLatitude;
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double pickupLongitude;
    @NotBlank private String pickupContactName;
    @NotBlank @Pattern(regexp = "^[0-9]{10,15}$")
    private String pickupContactPhone;

    @NotBlank
    private String deliveryAddress;
    @NotNull
    private Double deliveryLatitude;
    @NotNull
    private Double deliveryLongitude;
    @NotBlank
    private String recipientName;
    @NotBlank @Pattern(regexp = "^[0-9]{10,15}$")
    private String recipientPhone;

    @NotNull private Order.PackageType packageType;
    @NotNull @DecimalMin("0.1") @DecimalMax("100.0") private Double packageWeight;
    private String packageDimensions;
    @Size(max = 1000) private String specialInstructions;

    private LocalDateTime scheduledPickupTime;
    private LocalDateTime scheduledDeliveryTime;
    private String promoCode;
}

package com.dealership.inventory.dto;

import com.dealership.inventory.model.Vehicle;
import java.math.BigDecimal;

public class VehicleResponse {

    private Long id;
    private String make;
    private String model;
    private String category;
    private BigDecimal price;
    private Integer quantity;

    public static VehicleResponse from(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.id = vehicle.getId();
        response.make = vehicle.getMake();
        response.model = vehicle.getModel();
        response.category = vehicle.getCategory();
        response.price = vehicle.getPrice();
        response.quantity = vehicle.getQuantity();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }
}

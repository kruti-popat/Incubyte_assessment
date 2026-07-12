package com.dealership.inventory.service;

import com.dealership.inventory.dto.VehicleRequest;
import com.dealership.inventory.dto.VehicleResponse;
import com.dealership.inventory.exception.BadRequestException;
import com.dealership.inventory.exception.ResourceNotFoundException;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.VehicleRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleResponse createVehicle(VehicleRequest request) {
        Vehicle vehicle = mapToEntity(new Vehicle(), request);
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream().map(VehicleResponse::from).toList();
    }

    public List<VehicleResponse> searchVehicles(
            String make, String model, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        return vehicleRepository.searchVehicles(
                        emptyToNull(make),
                        emptyToNull(model),
                        emptyToNull(category),
                        minPrice,
                        maxPrice)
                .stream()
                .map(VehicleResponse::from)
                .toList();
    }

    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        Vehicle vehicle = findVehicle(id);
        mapToEntity(vehicle, request);
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    @Transactional
    public VehicleResponse purchaseVehicle(Long id) {
        Vehicle vehicle = findVehicle(id);
        if (vehicle.getQuantity() <= 0) {
            throw new BadRequestException("Vehicle is out of stock");
        }
        vehicle.setQuantity(vehicle.getQuantity() - 1);
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Transactional
    public VehicleResponse restockVehicle(Long id, int amount) {
        Vehicle vehicle = findVehicle(id);
        vehicle.setQuantity(vehicle.getQuantity() + amount);
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    private Vehicle findVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    private Vehicle mapToEntity(Vehicle vehicle, VehicleRequest request) {
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setCategory(request.getCategory());
        vehicle.setPrice(request.getPrice());
        vehicle.setQuantity(request.getQuantity());
        return vehicle;
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

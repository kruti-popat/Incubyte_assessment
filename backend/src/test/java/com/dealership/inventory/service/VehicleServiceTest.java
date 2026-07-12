package com.dealership.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dealership.inventory.dto.VehicleRequest;
import com.dealership.inventory.dto.VehicleResponse;
import com.dealership.inventory.exception.BadRequestException;
import com.dealership.inventory.exception.ResourceNotFoundException;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.VehicleRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setCategory("Sedan");
        vehicle.setPrice(new BigDecimal("28500.00"));
        vehicle.setQuantity(2);
    }

    @Test
    void purchaseVehicle_decreasesQuantity() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponse response = vehicleService.purchaseVehicle(1L);

        assertEquals(1, response.getQuantity());
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void purchaseVehicle_throwsWhenOutOfStock() {
        vehicle.setQuantity(0);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        assertThrows(BadRequestException.class, () -> vehicleService.purchaseVehicle(1L));
    }

    @Test
    void restockVehicle_increasesQuantity() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponse response = vehicleService.restockVehicle(1L, 5);

        assertEquals(7, response.getQuantity());
    }

    @Test
    void updateVehicle_throwsWhenNotFound() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        VehicleRequest request = new VehicleRequest();
        request.setMake("Honda");
        request.setModel("Civic");
        request.setCategory("Sedan");
        request.setPrice(new BigDecimal("25000"));
        request.setQuantity(3);

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.updateVehicle(99L, request));
    }
}

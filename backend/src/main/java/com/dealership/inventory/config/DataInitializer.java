package com.dealership.inventory.config;

import com.dealership.inventory.model.Role;
import com.dealership.inventory.model.User;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.UserRepository;
import com.dealership.inventory.repository.VehicleRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            VehicleRepository vehicleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setName("Admin User");
                admin.setEmail("admin@dealership.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);

                User user = new User();
                user.setName("Demo User");
                user.setEmail("user@dealership.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole(Role.USER);
                userRepository.save(user);
            }

            if (vehicleRepository.count() == 0) {
                vehicleRepository.save(createVehicle("Toyota", "Camry", "Sedan", new BigDecimal("28500.00"), 5));
                vehicleRepository.save(createVehicle("Honda", "CR-V", "SUV", new BigDecimal("32000.00"), 3));
                vehicleRepository.save(createVehicle("Ford", "Mustang", "Sports", new BigDecimal("45000.00"), 2));
                vehicleRepository.save(createVehicle("Tesla", "Model 3", "Electric", new BigDecimal("42000.00"), 0));
                vehicleRepository.save(createVehicle("BMW", "X5", "SUV", new BigDecimal("65000.00"), 4));
            }
        };
    }

    private Vehicle createVehicle(String make, String model, String category, BigDecimal price, int quantity) {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setCategory(category);
        vehicle.setPrice(price);
        vehicle.setQuantity(quantity);
        return vehicle;
    }
}

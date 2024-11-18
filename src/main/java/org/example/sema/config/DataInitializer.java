package org.example.sema.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.sema.entity.ApplicationUser;
import org.example.sema.entity.Device;
import org.example.sema.entity.Sensor;
import org.example.sema.repository.DeviceRepository;
import org.example.sema.repository.SensorDataRepository;
import org.example.sema.repository.SensorRepository;
import org.example.sema.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SensorRepository sensorRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        ApplicationUser user = new ApplicationUser();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEmail("lucka.scholz@gmail.com");

        Set<Device> devices = new HashSet<>();

        for (int i = 1; i <= 10; i++) {
            Device device = new Device();
            device.setDeviceName("Device " + i);
            device.setDescription("Description for Device " + i);

            deviceRepository.save(device);

            user.getDevices().add(device);

            for (int j = 1; j <= 2; j++) {
                Sensor sensor = new Sensor();
                sensor.setSensorName("Sensor " + i + j);
                sensor.setDescription("Description for Sensor " + j);
                sensor.setLatitude(50.0 + i + j);
                sensor.setLongitude(14.0 + i + j);
                sensor.setDevice(device);

                sensorRepository.save(sensor);
            }

            devices.add(device);
        }

        user.setDevices(devices);
        userRepository.save(user);

        System.out.println("Data initialized successfully!");
    }
}

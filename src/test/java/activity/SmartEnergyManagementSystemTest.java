package activity;

import org.junit.Before;
import org.junit.Test;

import activity.SmartEnergyManagementSystem.DeviceSchedule;
import activity.SmartEnergyManagementSystem.EnergyManagementResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import activity.FraudDetectionSystem.Transaction;
import activity.FraudDetectionSystem.FraudCheckResult;
import activity.FraudDetectionSystem;
//import static org.junit.jupiter.api.Assertions.*;

public class SmartEnergyManagementSystemTest {


    SmartEnergyManagementSystem system = new SmartEnergyManagementSystem();

    @Test
    public void testEnergySavingModeActivated() {
        // Cenário: Ativar o modo de economia de energia devido ao preço alto
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Lights", 2);
        devicePriorities.put("Heater", 1);
        devicePriorities.put("AirConditioner", 3);

        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 9, 10, 0);
        double currentPrice = 15.0;
        double priceThreshold = 10.0;
        double currentTemperature = 25.0;
        double[] desiredTemperatureRange = {18.0, 24.0};
        double energyUsageLimit = 20.0;
        double totalEnergyUsedToday = 15.0;
        List<DeviceSchedule> scheduledDevices = new ArrayList<>();

        EnergyManagementResult result = system.manageEnergy(currentPrice, priceThreshold, devicePriorities, currentTime, currentTemperature,
                desiredTemperatureRange, energyUsageLimit, totalEnergyUsedToday, scheduledDevices);

        assertTrue(result.energySavingMode);
        assertFalse(result.deviceStatus.get("Lights"));  // Desligado (prioridade > 1)
        assertTrue(result.deviceStatus.get("Heater"));  // Ligado (prioridade alta)
        assertFalse(result.deviceStatus.get("AirConditioner"));
    }

    @Test
    public void testNightModeActivation() {
        // Cenário: Ativação do modo noturno
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Lights", 1);
        devicePriorities.put("Security", 1);
        devicePriorities.put("Refrigerator", 1);

        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 9, 23, 30);
        double currentPrice = 8.0;
        double priceThreshold = 10.0;
        double currentTemperature = 20.0;
        double[] desiredTemperatureRange = {18.0, 24.0};
        double energyUsageLimit = 30.0;
        double totalEnergyUsedToday = 15.0;
        List<DeviceSchedule> scheduledDevices = new ArrayList<>();

        EnergyManagementResult result = system.manageEnergy(currentPrice, priceThreshold, devicePriorities, currentTime, currentTemperature,
                desiredTemperatureRange, energyUsageLimit, totalEnergyUsedToday, scheduledDevices);

        assertTrue(result.deviceStatus.get("Security"));
        assertTrue(result.deviceStatus.get("Refrigerator"));
        assertFalse(result.deviceStatus.get("Lights"));  // Desligado no modo noturno
    }

    @Test
    public void testTemperatureRegulationHeating() {
        // Cenário: Ativação do aquecimento
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Heater", 1);

        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 9, 12, 0);
        double currentPrice = 5.0;
        double priceThreshold = 10.0;
        double currentTemperature = 16.0;
        double[] desiredTemperatureRange = {18.0, 24.0};
        double energyUsageLimit = 50.0;
        double totalEnergyUsedToday = 20.0;
        List<DeviceSchedule> scheduledDevices = new ArrayList<>();

        EnergyManagementResult result = system.manageEnergy(currentPrice, priceThreshold, devicePriorities, currentTime, currentTemperature,
                desiredTemperatureRange, energyUsageLimit, totalEnergyUsedToday, scheduledDevices);

        assertTrue(result.temperatureRegulationActive);
        assertTrue(result.deviceStatus.get("Heater"));  // Aquecimento ativo
    }

    @Test
    public void testEnergyLimitExceeded() {
        // Cenário: Limite de energia ultrapassado
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Lights", 3);
        devicePriorities.put("Heater", 2);
        devicePriorities.put("AirConditioner", 1);

        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 9, 15, 0);
        double currentPrice = 7.0;
        double priceThreshold = 10.0;
        double currentTemperature = 22.0;
        double[] desiredTemperatureRange = {18.0, 24.0};
        double energyUsageLimit = 30.0;
        double totalEnergyUsedToday = 35.0;  // Limite ultrapassado
        List<DeviceSchedule> scheduledDevices = new ArrayList<>();

        EnergyManagementResult result = system.manageEnergy(currentPrice, priceThreshold, devicePriorities, currentTime, currentTemperature,
                desiredTemperatureRange, energyUsageLimit, totalEnergyUsedToday, scheduledDevices);

        assertFalse(result.deviceStatus.get("Lights"));  // Dispositivo desligado devido ao limite
        assertFalse(result.deviceStatus.get("Heater"));  // Desligado devido ao limite
    }

    @Test
    public void testDeviceScheduleActivation() {
        // Cenário: Ativação de dispositivo agendado
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("WashingMachine", 1);

        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 9, 16, 0);
        double currentPrice = 8.0;
        double priceThreshold = 10.0;
        double currentTemperature = 22.0;
        double[] desiredTemperatureRange = {18.0, 24.0};
        double energyUsageLimit = 40.0;
        double totalEnergyUsedToday = 10.0;
        List<DeviceSchedule> scheduledDevices = Arrays.asList(new DeviceSchedule("WashingMachine", currentTime));

        EnergyManagementResult result = system.manageEnergy(currentPrice, priceThreshold, devicePriorities, currentTime, currentTemperature,
                desiredTemperatureRange, energyUsageLimit, totalEnergyUsedToday, scheduledDevices);

        assertTrue(result.deviceStatus.get("WashingMachine"));  // Dispositivo ativado conforme o agendamento
    }

}

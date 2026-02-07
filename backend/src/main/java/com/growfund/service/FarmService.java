package com.growfund.service;

import com.growfund.dto.FarmDTO;
import com.growfund.model.Farm;
import com.growfund.model.User;
import com.growfund.repository.CropRepository;
import com.growfund.repository.FarmRepository;
import com.growfund.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FarmService {

    private final FarmRepository farmRepository;
    private final UserRepository userRepository;
    private final CropRepository cropRepository;

    @Transactional
    public FarmDTO createFarm(Long userId, String farmName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already has a farm
        Optional<Farm> existingFarm = farmRepository.findByUserId(userId);
        if (existingFarm.isPresent()) {
            throw new RuntimeException("User already has a farm");
        }

        Farm farm = new Farm();
        farm.setUser(user);
        farm.setFarmName(farmName);
        farm.setLandSize(1.0); // Default 1 acre
        farm.setSavings(0L);
        farm.setEmergencyFund(0L);

        Farm savedFarm = farmRepository.save(farm);
        return convertToDTO(savedFarm);
    }

    public FarmDTO getFarmByUserId(Long userId) {
        Farm farm = farmRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Farm not found for user"));
        return convertToDTO(farm);
    }

    @Transactional
    public FarmDTO updateFarmSavings(Long farmId, Long savings, Long emergencyFund) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found"));

        if (savings != null) {
            farm.setSavings(savings);
        }
        if (emergencyFund != null) {
            farm.setEmergencyFund(emergencyFund);
        }

        Farm updatedFarm = farmRepository.save(farm);
        return convertToDTO(updatedFarm);
    }

    public boolean isFarmOwnedByUser(Long farmId, Long userId) {
        return farmRepository.findById(farmId)
                .map(farm -> farm.getUser().getId().equals(userId))
                .orElse(false);
    }

    private FarmDTO convertToDTO(Farm farm) {
        FarmDTO dto = new FarmDTO();
        dto.setId(farm.getId());
        dto.setFarmName(farm.getFarmName());
        dto.setLandSize(farm.getLandSize());
        dto.setSavings(farm.getSavings());
        dto.setEmergencyFund(farm.getEmergencyFund());
        dto.setCropCount((int) cropRepository.countByFarmId(farm.getId()));
        return dto;
    }
}

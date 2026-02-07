package com.growfund.controller;

import com.growfund.dto.LeaderboardEntryDTO;
import com.growfund.model.Farm;
import com.growfund.repository.FarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final FarmRepository farmRepository;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard() {
        List<Farm> topFarms = farmRepository.findTop10ByOrderBySavingsDesc();
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();

        int rank = 1;
        for (Farm farm : topFarms) {
            String ownerName = (farm.getUser() != null) ? farm.getUser().getFullName() : "Unknown";
            leaderboard.add(new LeaderboardEntryDTO(
                    rank++,
                    farm.getFarmName(),
                    ownerName,
                    farm.getSavings()));
        }

        return ResponseEntity.ok(leaderboard);
    }
}

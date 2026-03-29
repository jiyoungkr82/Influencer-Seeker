package com.example.aimatching.Controller;

import com.example.aimatching.Entity.Influencer;
import com.example.aimatching.Repository.InfluencerRepository;
import com.example.aimatching.Service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MatchController {
    private final MatchService matchService;

    @GetMapping("/match")
    public List<Influencer> getMatch(@RequestParam String productName) {
        return matchService.findBestMatch(productName);
    }

}

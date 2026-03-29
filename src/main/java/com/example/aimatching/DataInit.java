package com.example.aimatching;

import com.example.aimatching.Entity.Influencer;
import com.example.aimatching.Repository.InfluencerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInit {
    private final InfluencerRepository repository;

    @PostConstruct
    public void init() {
        repository.save(new Influencer("테크왕", "IT", "MODERN"));
        repository.save(new Influencer("뷰티퀸", "BEAUTY",  "KITSCH"));
    }
}

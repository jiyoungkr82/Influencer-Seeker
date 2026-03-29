package com.example.aimatching.Repository;

import com.example.aimatching.Entity.Influencer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InfluencerRepository extends JpaRepository<Influencer, Long> {
    List<Influencer> findByCategory(String category);
    List<Influencer>findByCategoryAndStyle(String category, String style);
    List<Influencer>findByName(String name);
}

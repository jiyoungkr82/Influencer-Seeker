package com.example.aimatching.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Influencer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category; // IT, BEAUTY, FASHION
    private String style;    // KITSCH, CUTE, MODERN 등

    public Influencer(String name, String category, String style) {
        this.name = name;
        this.category = category;
        this.style = style;
    }
}

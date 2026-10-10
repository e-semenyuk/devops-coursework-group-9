package com.napier.group9;

public record CountryPopulation(
        String code,
        String name,
        String continent,
        String region,
        long population,
        String capital) {
}

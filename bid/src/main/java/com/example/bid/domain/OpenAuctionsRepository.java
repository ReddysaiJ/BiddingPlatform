package com.example.bid.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OpenAuctionsRepository extends JpaRepository<OpenAuctionsEntity, UUID> {
}

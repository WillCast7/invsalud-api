package com.aurealab.dto.CashRegister.response;

import com.aurealab.dto.CashRegister.CashSessionDTO;
import lombok.Builder;

import java.util.Set;

@Builder
public record CashSessionDetailsResponseDTO(
    CashSessionDTO cashSession,
    CashSessionSummaryDTO cashSessionSummary){}


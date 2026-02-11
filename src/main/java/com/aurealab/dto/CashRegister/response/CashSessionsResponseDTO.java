package com.aurealab.dto.CashRegister.response;

import com.aurealab.dto.CashRegister.CashSessionDTO;

public record CashSessionsResponseDTO(
    CashSessionDTO todaySession,
    CashSessionDTO openedSession
    ){}

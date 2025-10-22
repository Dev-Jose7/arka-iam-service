package com.arka.iam_service.session.domain.event.impl;

import com.arka.iam_service.session.domain.event.SessionEvent;
import com.arka.iam_service.session.domain.model.vo.JwtId;

import java.time.Instant;

public record TokenRotated(JwtId oldJti, JwtId newJti, Instant occurredAt) implements SessionEvent {}

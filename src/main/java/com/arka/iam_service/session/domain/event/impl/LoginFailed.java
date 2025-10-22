package com.arka.iam_service.session.domain.event.impl;

import com.arka.iam_service.session.domain.event.SessionEvent;

import java.time.Instant;

public record LoginFailed(String reason, Instant occurredAt) implements SessionEvent {}

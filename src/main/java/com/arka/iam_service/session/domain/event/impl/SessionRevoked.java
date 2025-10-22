package com.arka.iam_service.session.domain.event.impl;

import com.arka.iam_service.session.domain.event.SessionEvent;
import com.arka.iam_service.session.domain.model.vo.SessionId;

import java.time.Instant;

public record SessionRevoked (SessionId sessionId, Instant occurredAt) implements SessionEvent { }

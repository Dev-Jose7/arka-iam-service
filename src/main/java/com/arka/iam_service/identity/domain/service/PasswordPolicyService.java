package com.arka.iam_service.identity.domain.service;

import com.arka.iam_service.identity.domain.model.vo.RawPassword;

public interface PasswordPolicyService {
    void validate(RawPassword password);
}

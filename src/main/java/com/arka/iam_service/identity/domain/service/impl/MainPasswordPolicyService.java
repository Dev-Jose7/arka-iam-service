package com.arka.iam_service.identity.domain.service.impl;

import com.arka.iam_service.identity.domain.service.PasswordPolicyService;
import com.arka.iam_service.identity.domain.model.vo.RawPassword;

public class MainPasswordPolicyService implements PasswordPolicyService {

    @Override
    public void validate(RawPassword password) {
        String value = password.getValue();

        if (value.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        if (value.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        if (!value.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }

        if (!value.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        if (!value.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }
}

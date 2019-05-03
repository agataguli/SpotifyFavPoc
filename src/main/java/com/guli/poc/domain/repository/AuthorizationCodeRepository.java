package com.guli.poc.domain.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationCodeRepository {
    void addAuthorizationCode(String scope, String code);

    String getCode(String scope);
}

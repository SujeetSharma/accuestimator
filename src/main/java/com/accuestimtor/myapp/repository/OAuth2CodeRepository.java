package com.accuestimtor.myapp.repository;

import com.accuestimtor.myapp.domain.OAuth2AuthenticationCode;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OAuth2CodeRepository extends MongoRepository<OAuth2AuthenticationCode, String> {

    OAuth2AuthenticationCode findOneByCode(String code);
}

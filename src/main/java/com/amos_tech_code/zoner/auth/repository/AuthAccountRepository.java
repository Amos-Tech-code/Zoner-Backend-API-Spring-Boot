package com.amos_tech_code.zoner.auth.repository;

import com.amos_tech_code.zoner.auth.entity.AuthAccount;
import com.amos_tech_code.zoner.users.entity.User;
import com.amos_tech_code.zoner.users.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthAccountRepository extends JpaRepository<AuthAccount, UUID> {

    List<AuthAccount> findByUser(User user);

    Optional<AuthAccount> findByProviderAndProviderUserId(
            AuthProvider provider,
            String providerUserId
    );

    Optional<AuthAccount> findByUserIdAndProvider(
            UUID userId,
            AuthProvider provider
    );

}

package ru.blps.googleplay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.blps.googleplay.entity.UserAccount;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByIdAndActiveTrue(Long id);
}

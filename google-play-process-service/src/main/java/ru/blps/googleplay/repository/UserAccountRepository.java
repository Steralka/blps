package ru.blps.googleplay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.blps.googleplay.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
}

package ru.blps.googleplay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.blps.googleplay.entity.Installation;

import java.util.List;
import java.util.Optional;

public interface InstallationRepository extends JpaRepository<Installation, Long> {

    List<Installation> findByUserIdOrderByInstalledAtDesc(Long userId);

    Optional<Installation> findByIdAndUserId(Long id, Long userId);
}

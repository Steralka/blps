package ru.blps.googleplay.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.blps.googleplay.dto.InstallationResponse;
import ru.blps.googleplay.entity.Installation;

import java.util.List;
import java.util.Optional;

public interface InstallationRepository extends JpaRepository<Installation, Long> {

    @Query("""
        select new ru.blps.googleplay.dto.InstallationResponse(
            i.id,
            i.user.id,
            i.app.id,
            p.id,
            i.status,
            i.installedAt
        )
        from Installation i
        left join i.purchase p
        where i.user.id = :userId
        order by i.installedAt desc
        """)
    List<InstallationResponse> findResponsesByUserIdOrderByInstalledAtDesc(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"user", "app", "purchase"})
    Optional<Installation> findByIdAndUserId(Long id, Long userId);
}

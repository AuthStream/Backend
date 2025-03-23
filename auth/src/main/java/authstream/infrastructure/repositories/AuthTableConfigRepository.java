package authstream.infrastructure.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import authstream.domain.entities.AuthTableConfig;

@Repository
public interface AuthTableConfigRepository extends JpaRepository<AuthTableConfig, UUID> {
    default AuthTableConfig findFirst() {
        return findAll().stream().findFirst().orElse(null);
    }
}
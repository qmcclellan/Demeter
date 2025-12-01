package com.starkgrid.demeter.Repository;

import com.starkgrid.demeter.Model.Entity.RegistryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistryRepository extends JpaRepository<RegistryEntity, Long> {

    Optional<RegistryEntity> findByAbsolutePath(String absolutePath);

    Optional<RegistryEntity> findByHashSha256(String hashSha256);

    boolean existsByAbsolutePath(String absolutePath);
}


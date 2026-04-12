package com.teamsolution.inventory.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.inventory.entity.Brand;
import com.teamsolution.inventory.enums.BrandStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BrandRepository
    extends BaseRepository<Brand, UUID>, JpaSpecificationExecutor<Brand> {

  Optional<Brand> findByIsDeletedFalseAndStatusAndId(BrandStatus status, UUID id);
}

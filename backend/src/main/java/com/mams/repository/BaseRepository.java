package com.mams.repository;

import com.mams.entity.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaseRepository extends JpaRepository<Base, Long> {
    Optional<Base> findByBaseCode(String baseCode);
    boolean existsByBaseCode(String baseCode);
}

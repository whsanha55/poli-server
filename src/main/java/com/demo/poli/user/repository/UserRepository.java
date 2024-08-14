package com.demo.poli.user.repository;

import com.demo.poli.user.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    @Query("""
        SELECT u
        FROM User u
        WHERE u.userId = :userId
        and u.deleted = false
        """)
    Optional<UserEntity> findByUserId(String userId);
}

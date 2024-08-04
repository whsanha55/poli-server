package com.demo.poli.user.repository;

import com.demo.poli.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {

}

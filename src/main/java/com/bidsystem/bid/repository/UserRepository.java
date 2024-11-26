package com.bidsystem.bid.repository;

import com.bidsystem.bid.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Boolean existsByUsername(String username);
    
    UserEntity findByUsername(String username);
    
    UserEntity findByTelno(String telno);

    Boolean existsByTelno(String telno);

    Boolean existsByEmail(String email);
}

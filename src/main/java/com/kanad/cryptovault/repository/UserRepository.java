package com.kanad.cryptovault.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanad.cryptovault.entity.UserEntity;

public interface UserRepository extends JpaRepository <UserEntity, Long>{
	
	

 boolean existsByEmail(String email);
 
 Optional<UserEntity> findByEmail(String email);
	

}

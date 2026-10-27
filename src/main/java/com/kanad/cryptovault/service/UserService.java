package com.kanad.cryptovault.service;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kanad.cryptovault.entity.UserEntity;
import com.kanad.cryptovault.exception.EmailAlreadyExistsException;
import com.kanad.cryptovault.exception.UserNotFoundByEmailException;
import com.kanad.cryptovault.exception.UserNotFoundByIdException;
import com.kanad.cryptovault.repository.UserRepository;
import com.kanad.cryptovault.request.UserRequest;
import com.kanad.cryptovault.response.UserResponse;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;
	
	
	public UserResponse registerUser(UserRequest user) {
		
		UserEntity entity=convertToEntity(user);
		
		if(repository.existsByEmail(user.getEmail())) {
			
			throw new EmailAlreadyExistsException("email already existed");
		}
		
		
		UserEntity savedEntity=repository.save(entity);
		
		return convertToResponse(savedEntity);
		
	}
	
	
	
	public UserResponse getUserById(long id) {
		
		UserEntity user = repository.findById(id)
	            .orElseThrow(() ->
	                new UserNotFoundByIdException(
	                    "User with ID " + id + " not present"));
    return convertToResponse(user);
    
		
	}
	
	
	
	public UserResponse getUserByEmail(String email) {
		
		UserEntity userByEmail=repository.findByEmail(email)
				.orElseThrow(() ->
				 new UserNotFoundByEmailException("User with Email "+ email + " not present"));
		
		return convertToResponse(userByEmail);
		
		 
	}
	
	public List<UserResponse> getAllUsers(){
		
	

		List<UserEntity> users=repository.findAll();
		
		
		List<UserResponse> allUsers = new ArrayList<>();
		for(UserEntity user:users) {
			
			allUsers.add(convertToResponse(user));
				
		}
		
		return allUsers;
		
	}
	
	public void deleteUserById(long id) {
		
		Optional<UserEntity> deleteUser = repository.findById(id);
	    if (deleteUser.isEmpty()) {
	        throw new UserNotFoundByIdException("User with ID " + id + " not present");
	    }
	    
	  repository.deleteById(id);
		
	}
	
	
	public UserResponse convertToResponse(UserEntity user) {
		
		
		UserResponse response=new UserResponse();
		
		response.setId(user.getId());
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		response.setPhone(user.getPhone());
		response.setActive(user.getActive());
		
		return response;
		
	}
	
	
	public UserEntity convertToEntity(UserRequest request) {

	   
		UserEntity entityRequest=new UserEntity();
		
		entityRequest.setName(request.getName());
		entityRequest.setEmail(request.getEmail());
		entityRequest.setPhone(request.getPhone());
		entityRequest.setPassword(request.getPassword());
		
		return entityRequest;

	}
	
	

}

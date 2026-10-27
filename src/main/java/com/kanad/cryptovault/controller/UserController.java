package com.kanad.cryptovault.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kanad.cryptovault.entity.UserEntity;
import com.kanad.cryptovault.request.UserRequest;
import com.kanad.cryptovault.response.UserResponse;
import com.kanad.cryptovault.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@PostMapping()
	public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest newUser){
		
		UserResponse CreateAccount=userService.registerUser(newUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(CreateAccount);
		
	}
	
	
	@GetMapping()
	public ResponseEntity<List<UserResponse>> getUsers(){
		
		List<UserResponse> getAllUsers=userService.getAllUsers();
		
		return ResponseEntity.ok(getAllUsers);
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
		
		UserResponse response=userService.getUserById(id);
		return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("/email/{email}")
	public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email){
		
		UserResponse fetchUserByEmail=userService.getUserByEmail(email);
		return ResponseEntity.ok(fetchUserByEmail);
	}
	
	@DeleteMapping("/{id}")
	
	public ResponseEntity<String> deleteUser(@PathVariable 	Long id){
		
	userService.deleteUserById(id);
		return ResponseEntity.ok("User with ID " + id + " deleted successfully");
		
		
	}
	
	
}

package com.example.jwt1.repository;


import com.example.jwt1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// CRUD 함수를 JpaRepository가 들고 있음
// @Repository라는 어노테이션이 없어도 IoC된다. 이유는 JpaRepository를 상속했기 때문에
public interface UserRepository extends JpaRepository<User, Integer> {

	// findBy규칙 -> 그다음은 문법문제
	// select * from user where username = ? 
	// 이런 걸 Query Method라고 불림
	public User findByUsername(String username);	

}

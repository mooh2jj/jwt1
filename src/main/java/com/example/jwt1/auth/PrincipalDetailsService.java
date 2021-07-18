package com.example.jwt1.auth;

import com.example.jwt1.model.User;
import com.example.jwt1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// http://localhost:8082/login => 여기서 동작을 안한다
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername 실행됨");
        User userEntity = userRepository.findByUsername(username);
        System.out.println("userEntity: "+ userEntity);
        return new PrincipalDetails(userEntity);
    }
}

package com.bidsystem.bid.service;

import com.bidsystem.bid.dto.CustomUserDetails;
import com.bidsystem.bid.entity.UserEntity;
import com.bidsystem.bid.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userData = userRepository.findByTelno(username);             //전화번호로 사용자 로그인
        // UserEntity userData = userRepository.findByUsername(username);       //사용자 이름으로 로그인
        // 사용자 데이터 출력
        if (userData != null) {
            System.out.println("User found: " + userData);
            return new CustomUserDetails(userData);
        }
        if (userData != null) {
            return new CustomUserDetails(userData);
        }
        return new CustomUserDetails(userData);
    }
}

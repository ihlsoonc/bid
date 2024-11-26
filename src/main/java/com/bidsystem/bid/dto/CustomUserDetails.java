package com.bidsystem.bid.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bidsystem.bid.entity.UserEntity;

public class CustomUserDetails implements UserDetails {

    private  final UserEntity userEntity;
    
    public  CustomUserDetails(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collections = new ArrayList<>();

        collections.add(new GrantedAuthority() {
            
            @Override
            public String getAuthority(){
                return userEntity.getRole();
            }

        });
        return collections;

    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 사용자 정의 메서드
    public String getTelno() {
        return userEntity.getTelno(); // 전화번호 반환
    }

    public String getEmail() {
        return userEntity.getEmail(); // 이메일 반환
    }
    
}

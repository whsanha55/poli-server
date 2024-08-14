package com.demo.poli.user.service;

import com.demo.poli.global.exception.BaseException;
import com.demo.poli.user.entity.UserEntity;
import com.demo.poli.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserEntity getUser(String id) {
        return userRepository.findByUserId(id)
            .orElseThrow(() -> new BaseException("user not found", HttpStatus.UNAUTHORIZED));
    }

    public boolean isExist(String id) {
        return userRepository.findByUserId(id).isPresent();
    }

    public void validateUserIdNotExists(String userId) {
        if (isExist(userId)) {
            throw new BaseException("user already exist");
        }
    }
    @Transactional
    public UserEntity createUser(UserEntity user) {
        validateUserIdNotExists(user.getUserId());
        return userRepository.save(user);
    }
}

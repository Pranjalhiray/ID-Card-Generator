package com.idcard.service;

import com.idcard.entity.User;
import com.idcard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final IDCardService  idCardService;

    public AdminService(UserRepository userRepository, IDCardService idCardService) {
        this.userRepository = userRepository;
        this.idCardService  = idCardService;
    }

    @Transactional
    public void approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setStatus(User.Status.APPROVED);
        userRepository.save(user);
        idCardService.generateIDCard(user);
    }

    @Transactional
    public void rejectUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setStatus(User.Status.REJECTED);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}

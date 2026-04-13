package com.idcard.service;

import com.idcard.entity.User;
import com.idcard.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user: " + email));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                .build();
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new IllegalArgumentException("Email already registered.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);
        user.setStatus(User.Status.PENDING);
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }

    public List<User> getAllUsers()     { return userRepository.findAll(); }
    public List<User> getPendingUsers() { return userRepository.findByStatus(User.Status.PENDING); }
    public long countTotal()            { return userRepository.count(); }
    public long countPending()          { return userRepository.countByStatus(User.Status.PENDING); }
    public long countApproved()         { return userRepository.countByStatus(User.Status.APPROVED); }

    public User updateUserDetails(Long id, User incoming, MultipartFile photo) throws IOException {
        User current = getUserById(id);
        if (incoming.getFullName()    != null && !incoming.getFullName().isBlank())    current.setFullName(incoming.getFullName());
        if (incoming.getDepartment()  != null) current.setDepartment(incoming.getDepartment());
        if (incoming.getDesignation() != null) current.setDesignation(incoming.getDesignation());
        if (incoming.getPhoneNumber() != null) current.setPhoneNumber(incoming.getPhoneNumber());
        if (incoming.getBloodGroup()  != null) current.setBloodGroup(incoming.getBloodGroup());
        if (incoming.getDateOfBirth() != null) current.setDateOfBirth(incoming.getDateOfBirth());
        if (incoming.getAddress()     != null) current.setAddress(incoming.getAddress());
        if (incoming.getOrganization()!= null) current.setOrganization(incoming.getOrganization());
        if (incoming.getEmployeeId()  != null) current.setEmployeeId(incoming.getEmployeeId());
        if (photo != null && !photo.isEmpty()) current.setPhotoPath(savePhoto(photo));
        return userRepository.save(current);
    }

    private String savePhoto(MultipartFile file) throws IOException {
        Path dir = Paths.get("uploads/photos/");
        Files.createDirectories(dir);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return "photos/" + filename;
    }
}

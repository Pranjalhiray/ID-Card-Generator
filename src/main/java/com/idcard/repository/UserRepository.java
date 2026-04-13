package com.idcard.repository;

import com.idcard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByStatus(User.Status status);
    boolean existsByEmail(String email);
    long countByStatus(User.Status status);
}

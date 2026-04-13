package com.idcard.repository;

import com.idcard.entity.IDCard;
import com.idcard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IDCardRepository extends JpaRepository<IDCard, Long> {
    Optional<IDCard> findByUser(User user);
    Optional<IDCard> findByCardNumber(String cardNumber);
}

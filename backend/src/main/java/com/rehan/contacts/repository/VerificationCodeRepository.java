package com.rehan.contacts.repository;

import com.rehan.contacts.entity.User;
import com.rehan.contacts.entity.VerificationCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findTopByUserOrderByIdDesc(User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<VerificationCode> findFirstByUserOrderByIdDesc(User user);

    void deleteByUser(User user);
}

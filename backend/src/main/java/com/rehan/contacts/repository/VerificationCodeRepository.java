package com.rehan.contacts.repository;

import com.rehan.contacts.entity.User;
import com.rehan.contacts.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findTopByUserOrderByIdDesc(User user);

    void deleteByUser(User user);
}
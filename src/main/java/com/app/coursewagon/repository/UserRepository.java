package com.app.coursewagon.repository;

import com.app.coursewagon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUid(String uid);
}

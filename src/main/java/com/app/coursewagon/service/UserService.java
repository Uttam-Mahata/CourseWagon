package com.app.coursewagon.service;
import com.app.coursewagon.entity.User;
import com.app.coursewagon.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(String email, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        User newUser = new User();
        newUser.setUid(userRecord.getUid());
        newUser.setEmail(userRecord.getEmail());
        newUser.setDisplayName(userRecord.getDisplayName());

        return userRepository.save(newUser);
    }

    public Optional<User> authenticateUser(String uid) {
        return userRepository.findByUid(uid);
    }
}


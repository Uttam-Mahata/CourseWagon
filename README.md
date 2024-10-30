To implement a **Login/SignUp backend** using **Firebase** and **Spring Boot** with a **4-layer architecture**, we will create four layers: **Controller**, **Service**, **Repository**, and **Entity** layers. Firebase will be used for authentication, and Spring Boot will manage API requests and data persistence.

Here’s how we can set it up:

### 1. Prerequisites

1. **Firebase Project**: Set up a Firebase project from the [Firebase Console](https://console.firebase.google.com/), enable authentication (email/password), and download the `firebase-adminsdk` JSON file.
2. **Spring Boot Project**: Start a Spring Boot project with dependencies for:
   - Spring Web
   - Spring Data JPA
   - Firebase Admin SDK (add dependency manually)

### 2. Add Dependencies to `pom.xml`

Add the Firebase Admin SDK and Spring Security dependencies:

```xml
<dependencies>
    <!-- Firebase SDK -->
    <dependency>
        <groupId>com.google.firebase</groupId>
        <artifactId>firebase-admin</artifactId>
        <version>9.1.1</version>
    </dependency>
    
    <!-- Spring Boot and Spring Security dependencies -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Other dependencies as needed -->
</dependencies>
```

### 3. Configure Firebase in Spring Boot

In your Spring Boot application, initialize Firebase in the main application class. Place your Firebase JSON file in the `src/main/resources` directory.

```java
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class FirebaseAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirebaseAuthApplication.class, args);
    }

    @PostConstruct
    public void initFirebase() throws IOException {
        FileInputStream serviceAccount =
            new FileInputStream("src/main/resources/firebase-adminsdk.json");

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
```

### 4. Entity Layer

Define a `User` entity to persist user information in the database.

```java
import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uid;
    private String email;
    private String displayName;

    // Getters and setters
}
```

### 5. Repository Layer

Create a repository interface for CRUD operations on the `User` entity.

```java
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUid(String uid);
}
```

### 6. Service Layer

Create a `UserService` for handling business logic related to user management.

```java
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
```

### 7. Controller Layer

In the controller, define endpoints for user login and signup.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestParam String email, @RequestParam String password) {
        try {
            User newUser = userService.registerUser(email, password);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String uid) {
        Optional<User> user = userService.authenticateUser(uid);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }
}
```

### 8. Security Configuration (Optional)

For Firebase-based token validation, you may need to implement a filter to validate Firebase tokens on secure endpoints.

Here’s a basic configuration:

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated();
    }
}
```

To get the `firebase-adminsdk.json` file, follow these steps:

1. **Go to the Firebase Console**:
   - Open [Firebase Console](https://console.firebase.google.com/).
   - Log in with your Google account if prompted.

2. **Select or Create a Firebase Project**:
   - If you already have a Firebase project, click on it.
   - Otherwise, create a new project by clicking **Add Project**, following the setup instructions, and then returning to the project dashboard.

3. **Go to Project Settings**:
   - In your project dashboard, click on the **Settings gear icon** on the left side menu.
   - Select **Project Settings** from the dropdown.

4. **Generate Service Account Key**:
   - In Project Settings, go to the **Service accounts** tab.
   - Click on **Generate new private key** under the Firebase Admin SDK section.
   - A dialog box will appear to confirm. Click **Generate Key**.

5. **Download the JSON File**:
   - After you confirm, a JSON file with your Firebase Admin SDK credentials will be downloaded to your computer. This is your `firebase-adminsdk.json` file.

6. **Add the File to Your Project**:
   - Place the `firebase-adminsdk.json` file in your Spring Boot project under the `src/main/resources` directory. Make sure to reference this file correctly in your Firebase initialization code.

With this JSON file, your Spring Boot application can authenticate with Firebase services securely.

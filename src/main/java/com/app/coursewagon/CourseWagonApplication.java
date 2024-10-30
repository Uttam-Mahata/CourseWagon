package com.app.coursewagon;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class CourseWagonApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseWagonApplication.class, args);
    }

    @PostConstruct
    public void initFirebase() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/coursewagon-firebase-adminsdk-biym7-cbdb638690.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

}

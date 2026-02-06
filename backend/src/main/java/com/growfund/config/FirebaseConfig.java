package com.growfund.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            List<FirebaseApp> apps = FirebaseApp.getApps();
            if (apps.isEmpty()) {
                // Look for service-account.json in root or resources
                InputStream serviceAccount = null;
                try {
                    serviceAccount = new ClassPathResource("service-account.json").getInputStream();
                } catch (IOException e) {
                    // Fallback to checking file system if not in classpath
                    try {
                        serviceAccount = new FileInputStream("service-account.json");
                    } catch (IOException ex) {
                        System.out
                                .println("WARNING: service-account.json not found. Firebase will not work correctly.");
                        return;
                    }
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Application Initialized");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.github.oscarmgh.ecommerce_api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.oscarmgh.ecommerce_api.entity.Category;
import com.github.oscarmgh.ecommerce_api.entity.User;
import com.github.oscarmgh.ecommerce_api.repository.CategoryRepository;
import com.github.oscarmgh.ecommerce_api.repository.UserRepository;

import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminConfig adminConfig;

    public DataInitializer(UserRepository userRepository, CategoryRepository categoryRepository, 
                          PasswordEncoder passwordEncoder, AdminConfig adminConfig) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminConfig = adminConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if it doesn't exist
        if (userRepository.findByUsername(adminConfig.getUsername()).isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername(adminConfig.getUsername());
            adminUser.setEmail(adminConfig.getEmail());
            adminUser.setPassword(passwordEncoder.encode(adminConfig.getPassword()));
            adminUser.setRoles(Collections.singleton("ROLE_ADMIN"));
            userRepository.save(adminUser);
            System.out.println("Default admin user created: username=" + adminConfig.getUsername() + ", email=" + adminConfig.getEmail());
        }

        // Create default categories if they don't exist
        createDefaultCategory("Electronics", "Electronic devices and gadgets");
        createDefaultCategory("Clothing", "Apparel and fashion items");
        createDefaultCategory("Books", "Books and publications");
        createDefaultCategory("Home & Garden", "Home improvement and garden items");
        createDefaultCategory("Sports", "Sports equipment and accessories");
    }

    private void createDefaultCategory(String name, String description) {
        if (categoryRepository.findByName(name).isEmpty()) {
            Category category = new Category();
            category.setName(name);
            categoryRepository.save(category);
            System.out.println("Default category created: " + name);
        }
    }
}

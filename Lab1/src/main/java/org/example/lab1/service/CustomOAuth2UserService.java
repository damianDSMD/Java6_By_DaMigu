package org.example.lab1.service;

import org.example.lab1.entity.Role;
import org.example.lab1.entity.User;
import org.example.lab1.entity.UserRole;
import org.example.lab1.repository.RoleRepository;
import org.example.lab1.repository.UserRepository;
import org.example.lab1.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("==================== OAuth2 Login Started ====================");

        try {
            // Get OAuth2 user from Google
            OAuth2User oauth2User = super.loadUser(userRequest);
            logger.info("✅ Successfully loaded OAuth2User from Google");

            // Extract attributes from Google
            Map<String, Object> attributes = oauth2User.getAttributes();
            logger.info("📋 User attributes: {}", attributes);

            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String picture = (String) attributes.get("picture");
            String providerId = (String) attributes.get("sub");

            logger.info("📧 Email: {}", email);
            logger.info("👤 Name: {}", name);
            logger.info("🖼️ Picture: {}", picture);
            logger.info("🆔 Provider ID: {}", providerId);

            // Save or update user in database
            User savedUser = saveOrUpdateUser(email, name, picture, providerId);
            logger.info("✅ User saved successfully: {}", savedUser.getUsername());

            logger.info("==================== OAuth2 Login Completed ====================");
            return oauth2User;

        } catch (Exception e) {
            logger.error("❌ ERROR in OAuth2 login: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("Error processing OAuth2 user: " + e.getMessage());
        }
    }

    private User saveOrUpdateUser(String email, String name, String picture, String providerId) {
        logger.info("🔍 Checking if user exists with email: {}", email);

        // Check if user exists by email
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
        boolean isNewUser = false;

        if (existingUser.isPresent()) {
            logger.info("✅ User already exists, updating...");
            user = existingUser.get();
            user.setFullname(name);
            user.setPhoto(picture);
            user.setProvider("google");
            user.setProviderId(providerId);
        } else {
            logger.info("🆕 Creating new user...");
            user = new User();
            user.setUsername(email);
            user.setEmail(email);
            user.setFullname(name);
            user.setPhoto(picture);
            user.setProvider("google");
            user.setProviderId(providerId);
            user.setActivated(true);
            user.setAdmin(false);
            user.setPassword(null);
            isNewUser = true;
        }

        logger.info("💾 Saving user to database...");
        user = userRepository.save(user);
        logger.info("✅ User saved with username: {}", user.getUsername());

        // Assign USER role for new users
        if (isNewUser) {
            logger.info("🔑 Assigning USER role to new user...");
            Optional<Role> userRoleOpt = roleRepository.findById("USER");

            if (userRoleOpt.isEmpty()) {
                logger.error("❌ USER role not found in database!");
                throw new RuntimeException("USER role not found in database");
            }

            Role userRole = userRoleOpt.get();
            logger.info("✅ Found USER role: {}", userRole.getName());

            UserRole userRoleMapping = new UserRole();
            userRoleMapping.setUser(user);
            userRoleMapping.setRole(userRole);

            userRoleRepository.save(userRoleMapping);
            logger.info("✅ USER role assigned successfully");
        }

        return user;
    }
}
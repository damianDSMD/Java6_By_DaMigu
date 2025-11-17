package org.example.lab1.service;

import org.example.lab1.dao.RoleDAO;
import org.example.lab1.dao.UserDAO;
import org.example.lab1.dao.UserRoleDAO;
import org.example.lab1.entity.Role;
import org.example.lab1.entity.User;
import org.example.lab1.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Custom OAuth2 User Service
 * Processes OAuth2 login (Google, Facebook, etc.) and saves user to database
 */
@Service
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserRoleDAO userRoleDAO;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Get OAuth2 user info from provider (Google, Facebook, etc.)
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // Get provider name (google, facebook, etc.)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // Extract user attributes from OAuth2 response
        Map<String, Object> attributes = oauth2User.getAttributes();

        // Process user based on provider
        User user = processOAuth2User(provider, attributes);

        // Return OAuth2User with authorities
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"  // Use email as principal name
        );
    }

    /**
     * Process OAuth2 user and save/update in database
     */
    private User processOAuth2User(String provider, Map<String, Object> attributes) {
        String email = null;
        String name = null;
        String picture = null;
        String providerId = null;

        // Extract user info based on provider
        switch (provider.toLowerCase()) {
            case "google":
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                picture = (String) attributes.get("picture");
                providerId = (String) attributes.get("sub");  // Google user ID
                break;

            case "facebook":
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                picture = extractFacebookPicture(attributes);
                providerId = (String) attributes.get("id");
                break;

            default:
                throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        // Check if user already exists by email
        Optional<User> existingUser = userDAO.findByEmail(email);

        User user;
        if (existingUser.isPresent()) {
            // Update existing user
            user = existingUser.get();
            user.setFullname(name);
            user.setPhoto(picture);
            user.setProvider(provider);
            user.setProviderId(providerId);
            userDAO.save(user);
        } else {
            // Create new user
            user = new User();
            user.setUsername(generateUsername(email));
            user.setEmail(email);
            user.setFullname(name);
            user.setPhoto(picture);
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setPassword(null);  // OAuth2 users don't have password
            user.setActivated(true);
            user.setAdmin(false);

            // Save user
            user = userDAO.save(user);

            // Assign default USER role
            assignDefaultRole(user);
        }

        return user;
    }

    /**
     * Extract Facebook profile picture URL
     */
    @SuppressWarnings("unchecked")
    private String extractFacebookPicture(Map<String, Object> attributes) {
        if (attributes.containsKey("picture")) {
            Map<String, Object> pictureObj = (Map<String, Object>) attributes.get("picture");
            Map<String, Object> data = (Map<String, Object>) pictureObj.get("data");
            return (String) data.get("url");
        }
        return null;
    }

    /**
     * Generate unique username from email
     */
    private String generateUsername(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;

        // Ensure username is unique
        while (userDAO.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    /**
     * Assign default USER role to new OAuth2 user
     */
    private void assignDefaultRole(User user) {
        Optional<Role> userRole = roleDAO.findById("USER");

        if (userRole.isPresent()) {
            UserRole ur = new UserRole();
            ur.setUser(user);
            ur.setRole(userRole.get());
            userRoleDAO.save(ur);
        }
    }
}
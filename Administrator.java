import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

class Administrator extends User {
    private String adminId;
    private String securityLevel;

    public Administrator(String userId,
                         String username,
                         String password,
                         String name,
                         String email,
                         String contactInfo,
                         String adminId,
                         String securityLevel) {
        super(userId, username, password, name, email, contactInfo);
        if (adminId == null || adminId.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin ID is required");
        }
        if (securityLevel == null || securityLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Security level is required");
        }
        this.adminId = adminId.trim();
        this.securityLevel = securityLevel.trim();
    }

    @Override
    public boolean login(String username, String password) {
        return isActive() && getUsername().equals(username) && getPassword().equals(password);
    }

    @Override
    public void logout() {
        System.out.println("Administrator logged out successfully.");
    }

    @Override
    public void updateProfile(String newName, String newEmail, String newContactInfo) {
        if (newName != null && !newName.trim().isEmpty()) {
            setName(newName.trim());
        }
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            setEmail(newEmail.trim());
        }
        if (newContactInfo != null && !newContactInfo.trim().isEmpty()) {
            setContactInfo(newContactInfo.trim());
        }
        System.out.println("Administrator profile updated.");
    }

    public void createUser(User user, List<User> users) {
        if (!isActive()) {
            System.out.println("Cannot create new user; account is not active.");
            return;
        }
        if (user == null || users == null) {
            throw new IllegalArgumentException("Invalid user data or list");
        }
        users.add(user);
        System.out.println("New user created: " + user.getUsername());
    }

    public void modifySystemSettings(String settingName, String newValue) {
        if (!isActive()) {
            System.out.println("Cannot modify settings; account is not active.");
            return;
        }
        if (settingName == null || newValue == null ||
                settingName.trim().isEmpty() || newValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid setting name or new value");
        }

        try {
            // Use FileManager to modify system settings
            FileManager.saveSystemSettingsToFile(settingName, newValue);
            System.out.println("Setting modified: " + settingName + " → " + newValue);
        } catch (IOException e) {
            System.out.println("Error saving settings: " + e.getMessage());
        }
    }


    public void manageUserAccess(User user) {
        if (!isActive()) {
            System.out.println("Cannot manage user access; account is not active.");
            return;
        }
        if (user == null) {
            throw new IllegalArgumentException("Invalid user");
        }
        if (user.isActive()) {
            user.deactivate();
            System.out.println("User account deactivated: " + user.getUsername());
        } else {
            user.activate();
            System.out.println("User account activated: " + user.getUsername());
        }
    }

    // Getters and setters
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) {
        if (adminId == null || adminId.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin ID cannot be empty");
        }
        this.adminId = adminId.trim();
    }

    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) {
        if (securityLevel == null || securityLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Security level cannot be empty");
        }
        this.securityLevel = securityLevel.trim();
    }
}

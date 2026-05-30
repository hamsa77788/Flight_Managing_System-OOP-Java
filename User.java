public abstract class User {
    private String userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String contactInfo;
    private boolean isActive = true;

    public User(String userId, String username, String password, String name, String email, String contactInfo) {
        this.userId = userId;
        this.username = username;
        setPassword(password);
        this.name = name;
        this.email = email;
        this.contactInfo = contactInfo;
    }

    // Abstract methods
    public abstract boolean login(String username, String password);
    public abstract void logout();

    // Common method that can be overridden
    public void updateProfile(String name, String email, String contactInfo) {
        this.name = name;
        this.email = email;
        this.contactInfo = contactInfo;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getContactInfo() { return contactInfo; }
    public boolean isActive() { return isActive; }

    public void setPassword(String password) {
        if (password != null && password.length() >= 6) {
            this.password = password;
        } else {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
    }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setActive(boolean active) { this.isActive = active; }

    // Methods to activate/deactivate
    public void deactivate() { this.isActive = false; }
    public void activate() { this.isActive = true; }
}
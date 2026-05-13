package com.futura.FuturaFlow.dto;


public class UserProfileDTO {
    private String name;
    private String email;
    private String avatarUrl;

    public UserProfileDTO(String name, String email, String avatarUrl) {
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    // Гетери
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAvatarUrl() { return avatarUrl; }
}
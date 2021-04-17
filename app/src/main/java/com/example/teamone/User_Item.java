package com.example.teamone;

public class User_Item {
    String email;
    String profile_image;
    // 나중에 여기에 닉네임도 넣기
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public  String getProfile_image() { return profile_image; }
    public void setProfile_image(String profile_image) { this.profile_image = profile_image; }
}

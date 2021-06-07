package com.social.chatapp.storagedata;

public class Profile {

    private String name ;
    private String email ;
    private String profilePicture ;
    private Profile profile ;

    public Profile (){

    }

    public Profile(String name, String email, String profilePicture) {
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}

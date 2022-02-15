package com.exam.natour.Model;

public class AuthUser {
    private String name,email,token;
    private static AuthUser instance;

    public AuthUser() {
    }

    public static AuthUser getInstance(){
        if (instance == null){
            instance = new AuthUser();
        }
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void inizialize(){
        this.setToken(null);
        this.setEmail(null);
        this.setName(null);
    }
}

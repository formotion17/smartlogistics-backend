package com.enterprise.user.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User{
    
    private  UUID id;
    private  String name;
    private  String email;
    private  UserStatus status;
    private  LocalDateTime createdAt;

    public User(UUID id, String name, String email, UserStatus status, LocalDateTime createdAt){
        validateName(name);
        validateEmail(email);

        this.id=id;
        this.name=name;
        this.email=email;
        this.status=status;
        this.createdAt=createdAt;
    }

    private void validateName(String name){
        if(name==null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    private void validateEmail(String email){
        if(email == null || !email.contains(("@"))){
            throw new IllegalArgumentException("Invalid Email");
        }
    }

    public UUID getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public UserStatus getStatus(){
        return status;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    // --- SETTERS (Necesarios para la actualización del caso de uso PUT) ---
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }


}

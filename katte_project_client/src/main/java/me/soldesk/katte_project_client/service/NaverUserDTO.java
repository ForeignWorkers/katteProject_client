package me.soldesk.katte_project_client.service;

import lombok.Data;

@Data
public class NaverUserDTO {
    private String email;
    private String nickname;
    private String name;
    private String id;
    private String phoneNumber;
    private String birthday;
    private String birthyear;
}
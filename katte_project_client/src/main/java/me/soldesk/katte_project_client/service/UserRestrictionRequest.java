package me.soldesk.katte_project_client.service;

import lombok.Data;

@Data
public class UserRestrictionRequest {
    private int user_id;
    private String restriction_type;
    private int stop_days;
}

package com.project.habit_tracker_app.dto;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class ProfileDto {

    private Long id;
    private String userName;
    private String name;
    private String profilePic;


    public ProfileDto(Long id, String userName, String name, String profilePic) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.profilePic = profilePic;
    }

}

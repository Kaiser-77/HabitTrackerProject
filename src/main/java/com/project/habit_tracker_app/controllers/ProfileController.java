package com.project.habit_tracker_app.controllers;

import com.project.habit_tracker_app.auth.utils.AuthenticatedUserUtil;
import com.project.habit_tracker_app.dto.ProfileModificationDto;
import com.project.habit_tracker_app.responses.ProfilePageResponse;
import com.project.habit_tracker_app.entities.Profile;
import com.project.habit_tracker_app.services.ProfileService;
import com.project.habit_tracker_app.utils.AppConstant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class ProfileController {


    private final ProfileService profileService;
    private final AuthenticatedUserUtil authenticatedUserUtil;

    public ProfileController(ProfileService profileService, AuthenticatedUserUtil authenticatedUserUtil) {
        this.profileService = profileService;
        this.authenticatedUserUtil = authenticatedUserUtil;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello...!!");

    }

    @PutMapping("/updateProfile")
    public ResponseEntity<Profile> updateProfile(@RequestBody ProfileModificationDto profileModificationDto){
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        return ResponseEntity.ok(profileService.updateProfile(profileModificationDto,ownerId));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<ProfilePageResponse> getUsersFollowers(@PathVariable Long userId,
                                                                 @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                 @RequestParam(defaultValue = AppConstant.PAGE_SIZE_PROFILE, required = false) Integer pageSize){

        return  ResponseEntity.ok(profileService.getFollowersWithPagination(userId,pageNumber,pageSize));
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<ProfilePageResponse> getUsersFollowings(@PathVariable Long userId,
                                                                  @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                  @RequestParam(defaultValue = AppConstant.PAGE_SIZE_PROFILE, required = false) Integer pageSize){

        return  ResponseEntity.ok(profileService.getFollowingsWithPagination(userId,pageNumber,pageSize));
    }

    @PostMapping("/follow")
    public void follow( @RequestParam Long followId){
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        profileService.follow(ownerId, followId);
    }

    @PostMapping("/unfollow")
    public void unFollow( @RequestParam Long followId){
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        profileService.unFollow(ownerId, followId);
    }

    @GetMapping("/search/user")
    public ResponseEntity<ProfilePageResponse> searchUserByName(@RequestParam String name,
                                                                @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                @RequestParam(defaultValue = AppConstant.PAGE_SIZE_PROFILE, required = false) Integer pageSize){

        return ResponseEntity.ok(profileService.searchProfilesByName(name,pageNumber,pageSize));
    }

    @DeleteMapping("/deleteProfile")
    public void deleteUser( ){
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        profileService.deleteProfile(ownerId);
    }
}

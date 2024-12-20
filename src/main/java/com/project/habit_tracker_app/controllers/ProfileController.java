package com.project.habit_tracker_app.controllers;

import com.project.habit_tracker_app.auth.services.JwtService;
import com.project.habit_tracker_app.auth.services.RefreshTokenService;
import com.project.habit_tracker_app.auth.utils.AuthenticatedUserUtil;
import com.project.habit_tracker_app.dto.ProfileModificationDto;
import com.project.habit_tracker_app.responses.ProfilePageResponse;
import com.project.habit_tracker_app.entities.Profile;
import com.project.habit_tracker_app.services.ProfileService;
import com.project.habit_tracker_app.utils.AppConstant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class ProfileController {


    private final ProfileService profileService;
    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public ProfileController(ProfileService profileService, AuthenticatedUserUtil authenticatedUserUtil, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.profileService = profileService;
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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

        List<Long> followerIds = profileService.getFollowers(userId);
        return  ResponseEntity.ok(profileService.getProfileDtoResponsesByIds(followerIds,pageNumber,pageSize));
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<ProfilePageResponse> getUsersFollowings(@PathVariable Long userId,
                                                                  @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                  @RequestParam(defaultValue = AppConstant.PAGE_SIZE_PROFILE, required = false) Integer pageSize){

        List<Long> followingIds = profileService.getFollowings(userId);
        return  ResponseEntity.ok(profileService.getProfileDtoResponsesByIds(followingIds,pageNumber,pageSize));
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


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken,
                                    @RequestHeader("X-Refresh-Token") String refreshToken) {

        String accessToken = jwtService.extractToken(bearerToken);
        jwtService.blacklistAccessToken(accessToken);
        refreshTokenService.deleteRefreshToken(refreshToken);

        return ResponseEntity.ok("Logged out successfully");
    }
}

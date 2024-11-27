package com.project.habit_tracker_app.services;

import com.project.habit_tracker_app.auth.repositories.UserRepository;
import com.project.habit_tracker_app.dto.ProfileModificationDto;
import com.project.habit_tracker_app.dto.ProfileDto;
import com.project.habit_tracker_app.responses.ProfilePageResponse;
import com.project.habit_tracker_app.entities.Follower;
import com.project.habit_tracker_app.entities.Profile;
import com.project.habit_tracker_app.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final HabitRepository habitRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository, FollowerRepository followerRepository, PostRepository postRepository, LikeRepository likeRepository, CommentRepository commentRepository, HabitRepository habitRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.habitRepository = habitRepository;
    }


    public List<Profile> getAllUsers() {
        return profileRepository.findAll();
    }

    public void follow(Long userId, Long followingId){
        Optional<Follower> optionalFollower = followerRepository.findByUserIdAndFollowedId(userId,followingId);
        if (userId.equals(followingId)){
            throw new RuntimeException("User can't follow themself");
        } else if (optionalFollower.isPresent()){
            throw new RuntimeException("Already Followed");
        } else {
            Profile followingProfile = profileRepository.findById(followingId).orElseThrow(()-> new RuntimeException("no such profile found"));
            Profile profile = profileRepository.findById(userId).orElseThrow();
            Follower follower = new Follower();
            follower.setUserId(userId);
            follower.setFollowedId(followingId);
            follower.setCreatedAt(LocalDateTime.now());
            profile.setFollowing(profile.getFollowing() + 1);
            followingProfile.setFollowers(followingProfile.getFollowers() + 1);
            profileRepository.save(profile);
            profileRepository.save(followingProfile);

            followerRepository.save(follower);

        }
    }

    public void unFollow(Long userId, Long unfollowingId){
        Profile unfollowingProfile = profileRepository.findById(unfollowingId).orElseThrow(()-> new RuntimeException("no such profile found"));
        Profile profile = profileRepository.findById(userId).orElseThrow();
        profile.setFollowing(profile.getFollowing() - 1);
        unfollowingProfile.setFollowers(unfollowingProfile.getFollowers() - 1);
        profileRepository.save(profile);
        profileRepository.save(unfollowingProfile);

        followerRepository.deleteByUserIdAndFollowedId(userId,unfollowingId);
    }

    public List<Profile> getFollowers(Long profileId) {
        List<Long> profileIds = followerRepository.findFollowerProfileIds(profileId);
        return profileRepository.findAllById(profileIds);
    }
    public ProfilePageResponse getFollowersWithPagination(Long profileId, Integer pageNumber, Integer pageSize){

        List<Long> profileIds = followerRepository.findFollowerProfileIds(profileId);

        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<Profile> pageProfileList = profileRepository.findByIdIn(profileIds, pageable);
        List<Profile> profileList = pageProfileList.getContent();

        // Profile To ProfileNameDto
        List<ProfileDto> profileDtoList = new ArrayList<>();
        for(Profile profile : profileList){
            ProfileDto profileDto = new ProfileDto(
                    profile.getId(),
                    profile.getUserName(),
                    profile.getName(),
                    profile.getProfilePic()
            );
            profileDtoList.add(profileDto);
        }

        return new ProfilePageResponse(profileDtoList, pageNumber, pageSize,
                pageProfileList.getTotalElements(), pageProfileList.getTotalPages(), pageProfileList.isLast());
    }




    public List<Profile> getFollowings(Long profileId) {
        List<Long> profileIds = followerRepository.findFollowingProfileIds(profileId);
        return profileRepository.findAllById(profileIds);

    }
    public ProfilePageResponse getFollowingsWithPagination(Long profileId, Integer pageNumber, Integer pageSize){

        List<Long> profileIds = followerRepository.findFollowingProfileIds(profileId);

        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<Profile> pageProfileList = profileRepository.findByIdIn(profileIds, pageable);
        List<Profile> profileList = pageProfileList.getContent();

        // Profile To ProfileNameDto
        List<ProfileDto> profileDtoList = new ArrayList<>();
        for(Profile profile : profileList){
            ProfileDto profileDto = new ProfileDto(
                    profile.getId(),
                    profile.getUserName(),
                    profile.getName(),
                    profile.getProfilePic()
            );
            profileDtoList.add(profileDto);
        }

        return new ProfilePageResponse(profileDtoList, pageNumber, pageSize,
                pageProfileList.getTotalElements(), pageProfileList.getTotalPages(), pageProfileList.isLast());
    }

    

    public Profile getUserById(Long id) {
        return profileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Profile not found with id: " + id));
    }


    public ProfilePageResponse searchProfilesByName(String name, Integer pageNumber, Integer pageSize) {
//        return profileRepository.findByNameContainingIgnoreCase(name);

        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<Profile> pageProfileList = profileRepository.findByUsernameContainingIgnoreCase(name, pageable);
        List<Profile> profileList = pageProfileList.getContent();

        // Profile To ProfileNameDto
        List<ProfileDto> profileDtoList = new ArrayList<>();
        for(Profile profile : profileList){
            ProfileDto profileDto = new ProfileDto(
                    profile.getId(),
                    profile.getUserName(),
                    profile.getName(),
                    profile.getProfilePic()
            );
            profileDtoList.add(profileDto);
        }

        return new ProfilePageResponse(profileDtoList, pageNumber, pageSize,
                pageProfileList.getTotalElements(), pageProfileList.getTotalPages(), pageProfileList.isLast());

    }


    public Profile updateProfile(ProfileModificationDto profileModificationDto, Long ownerId){
        Profile profile = profileRepository.findById(ownerId).orElseThrow(()-> new RuntimeException("-----ERROR profile not found"));
        profile.setName(profileModificationDto.getName());
        profile.setAge(profileModificationDto.getAge());
        profile.setBio(profileModificationDto.getBio());
        profile.setProfilePic(profileModificationDto.getProfilePic());
        return profileRepository.save(profile);
    }

    @Transactional
    public void deleteProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId).orElseThrow();
        // Adjust follow counts
        profileRepository.decrementFollowingCountByProfileId(profileId);
        profileRepository.decrementFollowersCountByProfileId(profileId);

        // Adjust likes and cmt Counts
        postRepository.decrementLikesCountByProfileId(profileId);
        postRepository.decrementCommentCountByProfileId(profileId);

        // Delete related entities (posts, likes, comments, habits, followTable)
        followerRepository.deleteByUserIdOrFollowedId(profileId);
        likeRepository.deleteByPostIdIn(profile.getPostIds());   // To delete Likes of his Post
        commentRepository.deleteByPostIdIn(profile.getPostIds());   // To delete Comments of his Post
        likeRepository.deleteByUserLikedId(profileId);
        commentRepository.deleteByUserCommentedId(profileId);
        postRepository.deleteByProfileId(profileId);
        habitRepository.deleteByProfileId(profileId);

        // Finally, delete the profile and user
        profileRepository.deleteById(profileId);  // delete profile bfr user cuz foreignConstrnt
        userRepository.deleteById(profileId);
    }


}
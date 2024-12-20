package com.project.habit_tracker_app.controllers;

import com.project.habit_tracker_app.auth.utils.AuthenticatedUserUtil;
import com.project.habit_tracker_app.dto.*;
import com.project.habit_tracker_app.responses.CommentPageResponse;
import com.project.habit_tracker_app.entities.Post;
import com.project.habit_tracker_app.entities.Profile;
import com.project.habit_tracker_app.responses.PostPageResponse;
import com.project.habit_tracker_app.responses.ProfilePageResponse;
import com.project.habit_tracker_app.services.PostService;
import com.project.habit_tracker_app.services.ProfileService;
import com.project.habit_tracker_app.utils.AppConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("post")
public class PostController {

    private final PostService postService;
    private final ProfileService profileService;
    public final AuthenticatedUserUtil authenticatedUserUtil;

    public PostController(PostService postService, ProfileService profileService, AuthenticatedUserUtil authenticatedUserUtil) {
        this.postService = postService;
        this.profileService = profileService;
        this.authenticatedUserUtil = authenticatedUserUtil;
    }

    @GetMapping("/feed")
    public ResponseEntity<PostPageResponse> feedPosts(@RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                      @RequestParam(defaultValue = AppConstant.PAGE_SIZE_POST, required = false) Integer pageSize,
                                                      @RequestParam(defaultValue = AppConstant.SORT_BY_TIME, required = false) String sortBy,
                                                      @RequestParam(defaultValue = AppConstant.SORT_DIR, required = false) String dir){
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        List<Long> followingProfileIds = profileService.getFollowings(ownerId);

        return ResponseEntity.ok(postService.getFeedPostWithPaginationAndSorting(followingProfileIds,pageNumber,pageSize,sortBy,dir));
    }

    // get post likes
    @GetMapping("/{postId}/likes")
    public ResponseEntity<ProfilePageResponse> getPostLikers(@PathVariable Long postId,
                                                             @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                             @RequestParam(defaultValue = AppConstant.PAGE_SIZE_PROFILE, required = false) Integer pageSize){
        return ResponseEntity.ok(postService.getPostLikesWithPaginationAndSorting(postId,pageNumber,pageSize));
    }

    // get post comments
    @GetMapping("/{postId}/comments")
    public ResponseEntity<CommentPageResponse> getPostComments(@PathVariable Long postId,
                                                               @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                               @RequestParam(defaultValue = AppConstant.PAGE_SIZE_CMT, required = false) Integer pageSize,
                                                               @RequestParam(defaultValue = AppConstant.SORT_BY_TIME, required = false) String sortBy,
                                                               @RequestParam(defaultValue = AppConstant.SORT_DIR, required = false) String dir){
        return ResponseEntity.ok(postService.getPostCommentsWithTextWithPaginationAndSorting(postId,pageNumber,pageSize,sortBy,dir));
    }

    //----DON'T NEED OF OWNER'sID HERE IG
    @GetMapping("/{userId}/posts")
    public ResponseEntity<PostPageResponse> userPosts(@PathVariable Long userId,
                                                @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                @RequestParam(defaultValue = AppConstant.PAGE_SIZE_CMT, required = false) Integer pageSize,
                                                @RequestParam(defaultValue = AppConstant.SORT_BY_TIME, required = false) String sortBy,
                                                @RequestParam(defaultValue = AppConstant.SORT_DIR, required = false) String dir){
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        return ResponseEntity.ok(postService.getUserPost(userId,ownerId,pageNumber,pageSize,sortBy,dir));
    }


    @PostMapping("/like")
    public ResponseEntity<String> likePost(@RequestParam Long postId) {
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        postService.likePost(postId, ownerId);
        return ResponseEntity.ok("liked successfully");
    }

    @PostMapping("/unlike")
    public ResponseEntity<String> unlikePost(@RequestParam Long postId) {
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        postService.unLikePost(postId, ownerId);
        return ResponseEntity.ok("unliked post");
    }

//    @GetMapping("/count/like/{postId}")
//    public ResponseEntity<Integer> countLikes(@PathVariable Long postId) {
//        int count = postService.countLikes(postId);
//        return ResponseEntity.ok(count);
//    }


    @PostMapping("/comment")
    public ResponseEntity<String> commentPost(@RequestParam Long postId, @RequestParam String text) {
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        postService.commentPost(postId, ownerId, text);
        return ResponseEntity.ok("commented successfully");
    }

    @DeleteMapping("/delete/comment")
    public ResponseEntity<String> deleteCommentPost(@RequestParam Long cmtId) {
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        postService.deleteComment(cmtId, ownerId);
        return ResponseEntity.ok("commented deleted on post successfully");
    }

    // NO NEED FOR THIS COUNT FUNC TO BE SEPARATE -----------------------------------------------------------------------
//    @GetMapping("/count/comment/{postId}")
//    public ResponseEntity<Integer> countComments(@PathVariable Long postId) {
//        int count = postService.countComments(postId);
//        return ResponseEntity.ok(count);
//    }




    @PostMapping("/add/post")
    public ResponseEntity<Post> addPost(@RequestBody PostModificationDto postModificationDto){
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        Post post = postService.addPost(postModificationDto,ownerId);
        return new ResponseEntity<>(post,HttpStatus.CREATED);
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<Post> updateHabit(@PathVariable Long postId, @RequestBody PostModificationDto postModificationDto) {
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        Post post = postService.updatePost(postId, postModificationDto,ownerId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/deletePost/{postId}")
    public  void deletePost(@PathVariable Long postId){
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        postService.deletePost(postId,ownerId);
    }
}

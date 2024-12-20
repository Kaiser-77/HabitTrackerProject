package com.project.habit_tracker_app.services;

import com.project.habit_tracker_app.dto.*;
import com.project.habit_tracker_app.entities.*;
import com.project.habit_tracker_app.exceptions.InvalidOperationException;
import com.project.habit_tracker_app.exceptions.ResourceNotFoundException;
import com.project.habit_tracker_app.exceptions.UnauthorizedActionException;
import com.project.habit_tracker_app.repositories.CommentRepository;
import com.project.habit_tracker_app.repositories.LikeRepository;
import com.project.habit_tracker_app.repositories.PostRepository;
import com.project.habit_tracker_app.repositories.ProfileRepository;
import com.project.habit_tracker_app.responses.CommentPageResponse;
import com.project.habit_tracker_app.entities.Post;
import com.project.habit_tracker_app.responses.PostPageResponse;
import com.project.habit_tracker_app.responses.ProfilePageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;


    // setting id............need to rewrite code --------------------------------------------------------------------------------
    public Post addPost(PostModificationDto postModificationDto, Long ownerId){
        Profile profile = profileRepository.findById(ownerId).orElseThrow(()-> new ResourceNotFoundException("no profile found"));

        Post newPost = new Post();
        // picture to url
        newPost.setProfileId(ownerId);
        newPost.setPicture("");                             //SET PICTURE URL
        newPost.setCaption(postModificationDto.getCaption());
        newPost.setPrivacy(postModificationDto.getPrivacy());
        newPost.setCreatedAt(LocalDateTime.now());
//        newPost.setLikes(new HashSet<>());
//        newPost.setComments(new HashSet<>());
        Post savedPost = postRepository.save(newPost);
        profile.getPostIds().add(savedPost.getId());
        profileRepository.save(profile);
        return savedPost;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long postId){
        return postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("post not found"));
    }


    public PostPageResponse getUserPost(Long userId, Long ownerId, Integer pageNumber, Integer pageSize, String sortBy, String dir){

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Post> postPages;


        // If the viewer is the owner, show all habits (public and private)
        if (userId.equals(ownerId)) {
            postPages = postRepository.findByProfileId(userId,pageable);
        }
        // If the viewer is not the owner, only show public habits
        else {
            postPages = postRepository.findByProfileIdAndPrivacy(userId,Privacy.PUBLIC,pageable);
        }


        List<Post> posts = postPages.getContent();

        return new PostPageResponse(posts, pageNumber, pageSize,
                postPages.getTotalElements(),
                postPages.getTotalPages(),
                postPages.isLast());
    }


    public PostPageResponse getFeedPostWithPaginationAndSorting(List<Long> followingProfileIds, Integer pageNumber, Integer pageSize, String sortBy, String dir){

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                                                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);

        Page<Post> postPages = postRepository.findByProfileIdInAndPrivacy(followingProfileIds,Privacy.PUBLIC,pageable);
        List<Post> posts = postPages.getContent();


        return new PostPageResponse(posts, pageNumber, pageSize,
                                    postPages.getTotalElements(),
                                    postPages.getTotalPages(),
                                    postPages.isLast());

    }






    public void likePost(Long postId, Long ownerId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("no such post "));
        Optional<Likes> optionalLikes = likeRepository.findByPostIdAndUserLikedId(postId,ownerId);
        if(optionalLikes.isPresent()){
            throw new InvalidOperationException("post already liked");
        }else {
            Likes like = new Likes();
            like.setPostId(postId);
            like.setUserLikedId(ownerId);
            like.setCreatedAt(LocalDateTime.now());
            likeRepository.save(like);

            post.setLikeCount(post.getLikeCount() + 1);            //CASCADE.ALL in entity does this automatically
            postRepository.save(post);
        }
    }

//    @Transactional
    public void unLikePost(Long postId, Long ownerId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("no such post "));
        Likes like = likeRepository.findByPostIdAndUserLikedId(postId,ownerId).orElseThrow(()-> new EntityNotFoundException("Not liked yet"));
        likeRepository.delete(like);

        post.setLikeCount(post.getLikeCount() - 1);
        postRepository.save(post);
    }

    public void commentPost(Long postId, Long ownerId, String cmt){
        Optional<Post> OptionalPost = postRepository.findById(postId);
        if(OptionalPost.isPresent()){
            Post post = OptionalPost.get();
            Comment comment = new Comment();
            comment.setPostId(postId);
            comment.setUserCommentedId(ownerId);
            comment.setComment(cmt);
            comment.setCreatedAt(LocalDateTime.now());
            commentRepository.save(comment);

            post.setCommentCount(post.getCommentCount() + 1);
            postRepository.save(post);
        }else {
            throw new ResourceNotFoundException("no such post found");
        }
    }

//    @Transactional
    public void deleteComment(Long cmtId, Long ownerId){
        Comment comment = commentRepository.findById(cmtId).orElseThrow(() -> new ResourceNotFoundException("no comment founded"));
        Post post = postRepository.findById(comment.getPostId()).orElseThrow(() -> new ResourceNotFoundException("post not found"));
        if(comment.getUserCommentedId().equals(ownerId) || post.getProfileId().equals(ownerId)){
            commentRepository.delete(comment);

            post.setCommentCount(post.getCommentCount() - 1);
            postRepository.save(post);
        } else {
            throw new UnauthorizedActionException("can't delete other's comment");
        }

    }



    // update post
    public Post updatePost(Long postId, PostModificationDto postModificationDto, Long ownerId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("post not found"));
        if(post.getProfileId().equals(ownerId)) {
            post.setCaption(postModificationDto.getCaption());
            post.setPrivacy(postModificationDto.getPrivacy());
            return postRepository.save(post);
        } else{
            throw new UnauthorizedActionException("can't edit other user's post");
        }
    }

    @Transactional
    public void deletePost(Long postId,Long ownerId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("post not found"));
        Profile profile = profileRepository.findById(post.getProfileId()).orElseThrow(() -> new ResourceNotFoundException("--no profile found"));
        if (profile.getId().equals(ownerId)){
            profile.getPostIds().remove(postId);
            likeRepository.deleteByPostId(postId);     //delete all associated rows in like table
            commentRepository.deleteByPostId(postId);  //likewise in cmt table
            postRepository.delete(post);
            profileRepository.save(profile);
        } else {
            throw new UnauthorizedActionException("others post cant be deleted");
        }
    }

    public List<CommentDto> getPostCommentsWithText(Long postId){
        List<Object[]> commentObj = commentRepository.findPostComments(postId);

        return commentObj.stream()
                .map(result -> new CommentDto((Long) result[0],(String) result[1],(String) result[2], (String) result[3], (LocalDateTime) result[4]))
                .toList();
    }
    public CommentPageResponse getPostCommentsWithTextWithPaginationAndSorting(Long postId, Integer pageNumber, Integer pageSize, String sortBy, String dir){

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);

        Page<Object[]> pageCmtObj = commentRepository.findPostComments(postId, pageable);
        List<Object[]> cmtObjList = pageCmtObj.getContent();

        List<CommentDto> cmtDtoList = cmtObjList.stream()
                .map(result -> new CommentDto((Long) result[0],(String) result[1],(String) result[2], (String) result[3], (LocalDateTime) result[4]))
                .toList();

        return new CommentPageResponse(cmtDtoList, pageNumber, pageSize,
                                        pageCmtObj.getTotalElements(), pageCmtObj.getTotalPages(), pageCmtObj.isLast());
    }





    public List<ProfileDto> getPostLikes(Long postId){
        List<Object[]> likesObj = likeRepository.findPostLikes(postId);

        return likesObj.stream()
                .map(result -> new ProfileDto((Long) result[0],(String) result[1],(String) result[2],(String) result[3]))
                .toList();
    }
    public ProfilePageResponse getPostLikesWithPaginationAndSorting(Long postId, Integer pageNumber, Integer pageSize){

//        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
//                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<Object[]> pageProfileObj = likeRepository.findPostLikes(postId, pageable);
        List<Object[]> profileObjList = pageProfileObj.getContent();

        List<ProfileDto> profileDtoList = profileObjList.stream()
                .map(result -> new ProfileDto((Long) result[0],(String) result[1],(String) result[2],(String) result[3]))
                .toList();

        return new ProfilePageResponse(profileDtoList, pageNumber, pageSize,
                pageProfileObj.getTotalElements(), pageProfileObj.getTotalPages(), pageProfileObj.isLast());
    }

    // FOR CHECKING PURPOSE
    public List<Likes> getAllLikes() {
        return likeRepository.findAll();
    }

}
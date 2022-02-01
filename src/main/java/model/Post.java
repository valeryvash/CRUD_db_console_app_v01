package model;

import java.util.ArrayList;
import java.util.List;

public class Post implements Entity<Long> {
    private Long id;
    private String postContent;
    private PostStatus postStatus;
    private Long writer_id;
    private List<Tag> postTags;

    public Post() {
        this.id = -1L;
        this.postContent = "";
        this.postStatus = PostStatus.ACTIVE;
        this.writer_id = -1L;
        this.postTags = new ArrayList<>();
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostContent() {
        return this.postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public PostStatus getPostStatus() {
        return this.postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }

    public Long getWriter_id() {
        return this.writer_id;
    }

    public void setWriter_id(Long writer_id) {
        this.writer_id = writer_id;
    }

    public List<Tag> getPostTags() {
        return this.postTags;
    }

    public void setPostTags(List<Tag> postTags) {
        this.postTags = postTags;
    }

    public void addTag(Tag tag) {
        this.postTags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.postTags.remove(tag);
    }
}

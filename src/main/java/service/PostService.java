package service;

import model.Post;
import model.PostStatus;
import model.Tag;
import model.Writer;
import repository.PostRepository;

import java.util.stream.Stream;

public class PostService {

    private PostRepository postRepo;

    public PostService(PostRepository postRepo) {
        this.postRepo = postRepo;
    }

    private PostService() {}

    public void add(Post p) {
        postRepo.add(p);
    }

    public boolean contains(Post p) {
        return postRepo.contains(p);
    }

    public boolean containsId(Long id) {
        return postRepo.containsId(id);
    }

    public Post getById(Long id) {
        return postRepo.getById(id);
    }

    public void update(Post p) {
        postRepo.update(p);
    }

    public void delete(Post p) {
        postRepo.delete(p);
    }

    public void deleteById(Long id) {
        postRepo.deleteById(id);
    }

    public Stream<Post> getObjectsStream() {
        return postRepo.getObjectsStream();
    }

    public Long getFreeId() {
        return postRepo.getFreeId();
    }

    public Stream<Post> getPostsStreamByWriter(Writer w) {
        return postRepo.getPostsStreamByWriter(w);
    }

    public void deleteByStatus(PostStatus ps) {
        postRepo.deleteByStatus(ps);
    }

    public void deleteTagFromPost(Tag t) {
        postRepo.deleteTagFromPost(t);
    }
}

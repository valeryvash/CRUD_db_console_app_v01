package repository;

import model.Post;
import model.PostStatus;
import model.Tag;
import model.Writer;

import java.util.stream.Stream;

public interface PostRepository extends GenericRepository<Long, Post> {

    @Override
    void add(Post p);

    @Override
    boolean contains(Post p);

    @Override
    boolean containsId(Long id);

    @Override
    Post getById(Long id);

    @Override
    void update(Post p);

    @Override
    void delete(Post p);

    @Override
    void deleteById(Long id);

    @Override
    Stream<Post> getObjectsStream();

    @Override
    Long getFreeId();

    Stream<Post> getPostsStreamByWriter(Writer w);

    void deleteByStatus(PostStatus ps);

    void deleteTagFromPost(Tag t);
}

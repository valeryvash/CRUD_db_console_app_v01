package repository;

import model.Post;
import model.Tag;

import java.util.stream.Stream;

public interface TagRepository extends GenericRepository<Long,Tag>{
    @Override
    void add(Tag t) throws IllegalArgumentException;

    @Override
    boolean contains(Tag t);

    @Override
    boolean containsId(Long id);

    @Override
    Tag getById(Long id) throws IllegalArgumentException;

    @Override
    void update(Tag t) throws IllegalArgumentException;

    @Override
    void delete(Tag t);

    @Override
    void deleteById(Long id);

    @Override
    Stream<Tag> getObjectsStream();

    @Override
    Long getFreeId();

    boolean tagNameContains(String tagName);

    public Tag getByName(String tagName);

    public Stream<Tag> getTagsStreamForPost(Post p);
}

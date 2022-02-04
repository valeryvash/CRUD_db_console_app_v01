package service;

import model.Post;
import model.Tag;
import repository.TagRepository;

import java.util.stream.Stream;

public class TagService {

    private TagRepository tagRepo;

    public TagService(TagRepository tagRepo) {
        this.tagRepo = tagRepo;
    }

    private TagService(){}

    public void add(Tag t) {
        tagRepo.add(t);
    }

    public boolean contains(Tag t) {
        return tagRepo.contains(t);
    }

    public boolean containsId(Long id) {
        return tagRepo.containsId(id);
    }

    public Tag getById(Long id){
        return tagRepo.getById(id);
    }

    public void update(Tag t) {
        tagRepo.update(t);
    }

    public void delete(Tag t) {
        tagRepo.delete(t);
    }

    public void deleteById(Long id) {
        tagRepo.deleteById(id);
    }

    public Stream<Tag> getObjectsStream() {
        return tagRepo.getObjectsStream();
    }

    public Long getFreeId() {
        return tagRepo.getFreeId();
    }

    public boolean tagNameContains(String tagName) {
        return tagRepo.tagNameContains(tagName);
    }

    public Tag getByName(String tagName) {
        return tagRepo.getByName(tagName);
    }

    public Stream<Tag> getTagsStreamForPost(Post p) {
        return tagRepo.getTagsStreamForPost(p);
    }
}

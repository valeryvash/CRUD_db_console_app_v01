package controller;

import model.Post;
import model.PostStatus;
import model.Tag;
import model.Writer;
import repository.*;
import util.EntitiesPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostViewController {

    private final PostRepository pr = new JDBCPostRepositoryImpl();
    private final TagRepository tr = new JDBCTagRepositoryImpl();

    private final Scanner sc = new Scanner(System.in);
    private final EntitiesPrinter ep = new EntitiesPrinter();

    private final WriterViewController wvc = new WriterViewController();

    public void postCreate() {
        Writer w = wvc.getWriter();
        long writerId = w.getId();
        Post p = new Post();
        System.out.println("Input new post contain \n 'q' for quit");

        String s = sc.nextLine();
        if (s.equalsIgnoreCase("q")) System.exit(0);
        p.setPostContent(s);
        p.setWriter_id(writerId);
        p.setPostTags(getTagsList());
        pr.add(p);
        System.out.println("Post created");
        ep.print(w);
        ep.print(p);
    }

    private List<Tag> getTagsList() {
        List<Tag> tagsToBeStreamed = new ArrayList<>();
        System.out.println("Input tags. 's' for skip, 'q' for quit");
        do {
            String s = sc.nextLine();

            if (s.equalsIgnoreCase("q")) System.exit(0);
            if (s.equalsIgnoreCase("s")) break;

            if (tr.tagNameContains(s)){
                Tag t = tr.getByName(s);
                tagsToBeStreamed.add(t);
            } else {
                Tag t = new Tag();
                t.setTagName(s);
                tr.add(t);

                t = tr.getByName(s);
                tagsToBeStreamed.add(t);
            }
        } while (true);
        return tagsToBeStreamed;
    }

    public void getAllPosts() {
        pr.getObjectsStream().forEach(ep::print);
    }

    public void getPostsByStatus() {
        PostStatus ps = getPostStatus();
        pr.getObjectsStream()
                .filter(p -> p.getPostStatus() == ps)
                .forEach(ep::print);
    }

    private PostStatus getPostStatus() {
        System.out.println("Choose post status which you prefer\n" +
                "1. ACTIVE\n" +
                "2. DELETED\n" +
                " 'q' for quit");
        do {
            String s = sc.nextLine().toLowerCase();
            if (s.equals("q")) System.exit(0);
            switch (s) {
                case "1" -> {
                    return PostStatus.ACTIVE;
                }
                case "2" -> {
                    return PostStatus.DELETED;
                }
                default -> System.out.println("Wrong point. Input other");
            }
        } while (true);
    }

    public Post getPostById() {
        Post p = new Post();
        System.out.println("Input exist post id \n 'q' for quit");
        do {
            String s = sc.nextLine();
            if (s.equalsIgnoreCase("q")) System.exit(0);
                try {
                    Long id = Long.valueOf(s);
                    if (pr.containsId(id)) {
                        p = pr.getById(id);
                        ep.print(p);
                        return p;
                    }
                } catch (NumberFormatException ignored) {}
            System.out.println("Such post doesn't exist! Try again");
        } while (true);
    }

    public void getPostsByTags() {
        List<Tag> tagsList = getTagsList();
        if (tagsList.isEmpty()) {
            pr.getObjectsStream()
                    .filter(p ->  p.getPostTags().stream().findAny().isEmpty())
                    .forEach(ep::print);
        } else {
            List<String> tagNames = tagsList.stream().map(Tag::getTagName).collect(Collectors.toList());
            pr.getObjectsStream()
                    .filter(p -> p.getPostTags().stream().anyMatch(
                            tag -> {
                                String tagName = tag.getTagName();
                                return tagNames.stream().anyMatch(tagName::equalsIgnoreCase);
                            }
                    ))
                    .forEach(ep::print);
        }
    }

    public void updatePostContentById() {
        Post p = getPostById();

        System.out.println("Input new post contain \n 'q' for quit");

        String s = sc.nextLine();
        if (s.equalsIgnoreCase("q")) System.exit(0);
        p.setPostContent(s);

        pr.update(p);
        System.out.println("Post content updated");
        ep.print(p);
    }


    public void updatePostTagsById() {
        Post p = getPostById();
        Stream<Tag> tagStream = getTagsList().stream();
        p.setPostTags(tagStream.collect(Collectors.toList()));
        pr.update(p);
        System.out.println("Post tags updated");
        ep.print(p);
    }

    public void changePostStatusById() {
        Post p = getPostById();
        PostStatus ps = getPostStatus();
        p.setPostStatus(ps);
        pr.update(p);
        ep.print(p);

    }

    public void deletePostById() {
        Post p = getPostById();
        pr.delete(p);
        System.out.println("Post deleted");
    }

    public void deletePostsByStatus() {
        PostStatus ps = getPostStatus();
        pr.deleteByStatus(ps);
        System.out.println("Posts deleted");
    }
}

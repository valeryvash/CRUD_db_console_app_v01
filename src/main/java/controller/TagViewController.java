package controller;

import model.Tag;
import repository.*;
import util.EntitiesPrinter;


import java.util.Scanner;

public class TagViewController {

    private final TagRepository tr = new JDBCTagRepositoryImpl();

    private final Scanner sc = new Scanner(System.in);

    private final EntitiesPrinter ep = new EntitiesPrinter();

    public void printAllTags() {
        tr.getObjectsStream().forEach(ep::print);
    }

    public void updateTag() {
        Tag t = getTag();
        do {
            System.out.println("Input new tag name");
            String s = sc.nextLine();
            if (s.equalsIgnoreCase("q")) System.exit(0);

            if (!tr.tagNameContains(s)) {
                t.setTagName(s);
                break;
            }
            System.out.println("Such tag name already exist! Try again");
        } while (true);
        tr.update(t);
        System.out.println("Tag successfully updated");
    }

    public void deleteTag() {
        Tag t = getTag();
        tr.delete(t);
    }

    private Tag getTag() {
        System.out.println("Input exist tag id or name \n 'q' for quit");
        do {
            String s = sc.nextLine();
            if (s.equalsIgnoreCase("q")) System.exit(0);
            if (!tr.tagNameContains(s)) {
                try {
                    Long id = Long.valueOf(s);
                    if (tr.containsId(id)){
                        Tag t = tr.getById(id);
                        ep.print(t);
                        return t;
                    }
                } catch (NumberFormatException ignored) {}
            } else {
                Tag t = tr.getByName(s);
                ep.print(t);
                return t;
            }
            System.out.println("Such tag doesn't exist! Try again");
        } while (true);
    }
}

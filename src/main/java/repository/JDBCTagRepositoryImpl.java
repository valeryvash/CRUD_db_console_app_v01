package repository;

import model.Post;
import model.Tag;
import util.PreparedStatementProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JDBCTagRepositoryImpl implements TagRepository {

    private Tag getTagFromResultSet(ResultSet resultSet) {
        Tag t = new Tag();
        try {
            if (resultSet.next()) {
                t.setId(resultSet.getLong("tag_id"));
                t.setTagName(resultSet.getString("tag_name"));
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }

    protected static List<Tag> getListOfTagsFromResultSet(ResultSet resultSet) {
        List<Tag> tags = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Tag t = new Tag();
                t.setId(resultSet.getLong("tag_id"));
                t.setTagName(resultSet.getString("tag_name"));
                tags.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }


    @Override
    public void add(Tag t) throws IllegalArgumentException {
        if (t.getId() != -1L) {
            throw new IllegalArgumentException("Tag id value is " + t.getId() + " , but shall be -1L");
        }

        try (
                PreparedStatement tagsTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "INSERT INTO tags (tag_name) VALUE (?);"
                        );
        ) {
            tagsTableQuery.setString(1, t.getTagName());
            tagsTableQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean contains(Tag t) {
        return this.containsId(t.getId());
    }

    @Override
    public boolean containsId(Long id) {
        boolean isTagIdExist = false;

        try (
                PreparedStatement tagsTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT COUNT(tag_name) as count FROM tags WHERE tag_id = ? ;"
                        )
        ) {
            tagsTableQuery.setLong(1, id);
            ResultSet answerToQuery = tagsTableQuery.executeQuery();
            if (answerToQuery.next()) {
                isTagIdExist = answerToQuery.getInt("count") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isTagIdExist;
    }

    @Override
    public Tag getById(Long id) throws IllegalArgumentException {
        if (id < 1L) {
            throw new IllegalArgumentException("Id shall be positive value");
        }
        Tag tagToBeReturned = new Tag();
        try (
                PreparedStatement tagsTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT tag_id,tag_name FROM tags WHERE tag_id = ?;"
                        )
        ) {
            tagsTableQuery.setLong(1, id);
            ResultSet answerQuery = tagsTableQuery.executeQuery();
            tagToBeReturned = this.getTagFromResultSet(answerQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tagToBeReturned;
    }

    @Override
    public void update(Tag t) throws IllegalArgumentException {
        if (t.getId() < 1L) {
            throw new IllegalArgumentException("Id shall be positive value");
        }
        try (
                PreparedStatement tagTableUpdateQuery =
                        PreparedStatementProvider.prepareStatement(
                                "UPDATE tags SET tags.tag_name = ? " +
                                        "WHERE tags.tag_id = ? ;"
                        )
        ) {
            tagTableUpdateQuery.setString(1, t.getTagName());
            tagTableUpdateQuery.setLong(2, t.getId());
            tagTableUpdateQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Tag t) {
        this.deleteById(t.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id < 1L) {
            throw new IllegalArgumentException("Id shall be positive value");
        }
        try (
                PreparedStatement deleteTagTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "DELETE FROM tags WHERE tags.tag_id = ? ;"
                        )
        ) {
            deleteTagTableQuery.setLong(1, id);
            deleteTagTableQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stream<Tag> getObjectsStream() {
        List<Tag> tagsToBeStreamed = new ArrayList<>();
        try (
                PreparedStatement tagsTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT tag_id,tag_name FROM tags;"
                        )
        ) {
            ResultSet answerToQuery = tagsTableQuery.executeQuery();
            tagsToBeStreamed = this.getListOfTagsFromResultSet(answerToQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tagsToBeStreamed.stream();
    }

    @Override
    public Long getFreeId() {
        long idToBeReturned = -1L;
        try (
                PreparedStatement queryToDataBase = PreparedStatementProvider
                        .prepareStatement(
                                "SELECT MAX(tag_id) + 1 as freeId " +
                                        "FROM tags ;"
                        )
        ) {
            ResultSet answerToQuery = queryToDataBase.executeQuery();
            idToBeReturned = answerToQuery.getInt("freeId");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idToBeReturned;
    }

    @Override
    public boolean tagNameContains(String tagName) {
        boolean isTagNameExist = false;
        try (
                PreparedStatement tagsTableQuery =
                        PreparedStatementProvider
                                .prepareStatement(
                                        "SELECT COUNT(tag_id) as count FROM tags " +
                                                "WHERE tag_name = ? ;"
                                )
        ) {
            tagsTableQuery.setString(1, tagName);
            ResultSet answerToQuery = tagsTableQuery.executeQuery();
            if (answerToQuery.next()) {
                isTagNameExist = answerToQuery.getInt("count") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isTagNameExist;
    }

    @Override
    public Tag getByName(String tagName) {
        Tag tagToBeReturned = new Tag();
        try (
                PreparedStatement tagsTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT tag_id,tag_name FROM tags " +
                                        "WHERE tag_name = ? ;"
                        );
        ) {
            tagsTableQuery.setString(1, tagName);
            ResultSet answerToQuery = tagsTableQuery.executeQuery();
            tagToBeReturned = this.getTagFromResultSet(answerToQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tagToBeReturned;
    }

    @Override
    public Stream<Tag> getTagsStreamForPost(Post p) {
        List<Tag> tagsToBeStreamed = new ArrayList<>();
        try (
                PreparedStatement tagsTableQuery =
                        PreparedStatementProvider
                                .prepareStatement(
                                        "SELECT tag_id,tag_name " +
                                                " FROM tags t INNER JOIN post_tag_relation ptr " +
                                                " ON t.tag_id = ptr.fk_tag_id " +
                                                " WHERE ptr.fk_post_id = ? ;"
                                )
        ) {
            tagsTableQuery.setLong(1,p.getId());
            ResultSet answerToQuery = tagsTableQuery.executeQuery();
            tagsToBeStreamed = this.getListOfTagsFromResultSet(answerToQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tagsToBeStreamed.stream();
    }
}

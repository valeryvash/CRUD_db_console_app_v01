package repository;

import model.Post;
import model.PostStatus;
import model.Tag;
import model.Writer;
import util.PreparedStatementProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JDBCPostRepositoryImpl implements PostRepository {

    public Post getPostFromResultSet(ResultSet resultSet) {
        Post p = new Post();
        try {
            if (resultSet.next()) {
                p.setId((long) resultSet.getInt("post_id"));
                p.setPostContent(resultSet.getString("post_content"));
                p.setPostStatus(PostStatus.valueOf(resultSet.getString("post_status")));
                p.setWriter_id((long) resultSet.getInt("fk_writer_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    public List<Post> getListOfPostsFromResultSet(ResultSet resultSet) {
        List<Post> posts = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Post p = new Post();
                p.setId((long) resultSet.getInt("post_id"));
                p.setPostContent(resultSet.getString("post_content"));
                p.setPostStatus(PostStatus.valueOf(resultSet.getString("post_status")));
                p.setWriter_id((long) resultSet.getInt("fk_writer_id"));
                posts.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public void add(Post p) {
        if (p.getId() != -1L) {
            throw new IllegalArgumentException("Post id value is " + p.getId() + " , but shall be -1L");
        }
        try (PreparedStatement postTableQuery = PreparedStatementProvider.prepareStatement(
                "INSERT INTO posts (post_content, post_status, fk_writer_id) VALUE (?,?,?) ;",
                PreparedStatement.RETURN_GENERATED_KEYS
        );
             PreparedStatement postTagRelationQuery = PreparedStatementProvider.prepareStatement(
                     "INSERT INTO post_tag_relation (fk_post_id, fk_tag_id) VALUES (?,?) ;"
             );) {
            postTableQuery.setString(1, p.getPostContent());
            postTableQuery.setString(2, p.getPostStatus().name());
            postTableQuery.setLong(3, p.getWriter_id());
            postTableQuery.executeUpdate();

            ResultSet generatedId = postTableQuery.getGeneratedKeys();
            long generatedPostId = -1L;
            if (generatedId.next()) {
                generatedPostId = generatedId.getLong("GENERATED_KEY");
            }

            List<Tag> postTags = p.getPostTags();
            if (!postTags.isEmpty()) {
                Connection currentConnection = postTagRelationQuery.getConnection();
                boolean batchUpdatesSupported = currentConnection.getMetaData().supportsBatchUpdates();
                if (batchUpdatesSupported) {
                    currentConnection.setAutoCommit(false);
                    for (Tag t : postTags) {
                        postTagRelationQuery.setLong(1, generatedPostId);
                        postTagRelationQuery.setLong(2, t.getId());
                        postTagRelationQuery.addBatch();
                    }
                    postTagRelationQuery.executeBatch();
                    currentConnection.commit();

                    currentConnection.setAutoCommit(true);
                } else {
                    for (Tag t : postTags) {
                        postTagRelationQuery.setLong(1, generatedPostId);
                        postTagRelationQuery.setLong(2, t.getId());
                        postTagRelationQuery.executeUpdate();
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean contains(Post p) {
        return this.containsId(p.getId());
    }

    @Override
    public boolean containsId(Long id) {
        boolean isPostExist = false;
        try (
                PreparedStatement containsPostQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT COUNT(post_id) as count FROM posts " +
                                        "WHERE post_id = ? ;"
                        )
        ) {
            containsPostQuery.setLong(1, id);
            ResultSet answerToQuery = containsPostQuery.executeQuery();
            if (answerToQuery.next()) {
                isPostExist = answerToQuery.getInt("count") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isPostExist;
    }

    @Override
    public Post getById(Long id) {
        if (id < 1L) {
            throw new IllegalArgumentException("Id shall be positive value");
        }
        Post postToBeReturned = new Post();
        try (
                PreparedStatement postsTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT post_id,post_content,post_status,fk_writer_id " +
                                        "FROM posts " +
                                        "WHERE post_id = ? ;"
                        );
                PreparedStatement postTagsTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT tag_id,tag_name " +
                                        " FROM tags t INNER JOIN post_tag_relation ptr " +
                                        " ON t.tag_id = ptr.fk_tag_id " +
                                        " WHERE ptr.fk_post_id = ? ;"
                        );
        ) {
            postsTableQuery.setLong(1, id);
            ResultSet answerToPostsTableQuery = postsTableQuery.executeQuery();
            postToBeReturned = this.getPostFromResultSet(answerToPostsTableQuery);

            postTagsTableQuery.setLong(1, postToBeReturned.getId());
            ResultSet answerToPostTagsQuery = postTagsTableQuery.executeQuery();
            List<Tag> tagListForPost =
                    JDBCTagRepositoryImpl.getListOfTagsFromResultSet(answerToPostTagsQuery);
            postToBeReturned.setPostTags(tagListForPost);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postToBeReturned;
    }

    @Override
    public void update(Post p) {
        if (p.getId() < 1L) {
            throw new IllegalArgumentException("Id shall be positive value");
        }
        try (
                PreparedStatement updatePostQuery =
                        PreparedStatementProvider.prepareStatement(
                                "UPDATE posts SET " +
                                        "post_content = ? ," +
                                        "post_status = ? ," +
                                        "fk_writer_id = ? " +
                                        "WHERE post_id = ? ;"
                        );
                PreparedStatement deletePostTagsRelations =
                        PreparedStatementProvider.prepareStatement(
                                "DELETE FROM post_tag_relation " +
                                        "WHERE post_tag_relation.fk_post_id = ?;"
                        );
                PreparedStatement insertPostTagRelation =
                        PreparedStatementProvider.prepareStatement(
                                "INSERT INTO post_tag_relation (fk_post_id, fk_tag_id) VALUE (?,?);"
                        )
        ) {
            updatePostQuery.setString(1, p.getPostContent());
            updatePostQuery.setString(2, p.getPostStatus().name());
            updatePostQuery.setLong(3, p.getWriter_id());
            updatePostQuery.setLong(4, p.getId());
            updatePostQuery.executeUpdate();

            deletePostTagsRelations.setLong(1, p.getId());
            deletePostTagsRelations.executeUpdate();

            long postId = p.getId();
            List<Tag> postTags = p.getPostTags();
            Connection currentConnection = insertPostTagRelation.getConnection();
            boolean batchUpdatesSupported = currentConnection.getMetaData().supportsBatchUpdates();
            if (batchUpdatesSupported){
                currentConnection.setAutoCommit(false);
                for (Tag t : postTags) {
                    long tagId = t.getId();
                    insertPostTagRelation.setLong(1, postId);
                    insertPostTagRelation.setLong(2, tagId);
                    insertPostTagRelation.addBatch();
                }
                insertPostTagRelation.executeBatch();
                currentConnection.commit();

                currentConnection.setAutoCommit(true);
            } else {
                for (Tag t : postTags) {
                    long tagId = t.getId();
                    insertPostTagRelation.setLong(1, postId);
                    insertPostTagRelation.setLong(2, tagId);
                    insertPostTagRelation.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Post p) {
        this.deleteById(p.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id < 1L) {
            throw new IllegalArgumentException("Id shall be a positive value");
        }
        try (
                PreparedStatement deletePostQuery =
                        PreparedStatementProvider.prepareStatement(
                                "DELETE FROM posts WHERE post_id = ? ;"
                        );
                PreparedStatement deletePostTagRelation =
                        PreparedStatementProvider.prepareStatement(
                                "DELETE FROM post_tag_relation WHERE fk_post_id = ? ;"
                        )
        ) {
            deletePostQuery.setLong(1, id);
            deletePostQuery.executeUpdate();

            deletePostTagRelation.setLong(1, id);
            deletePostTagRelation.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stream<Post> getObjectsStream() {
        List<Post> postsToBeStreamed = new ArrayList<>();
        try (
                PreparedStatement postTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT post_id, post_content, post_status, fk_writer_id " +
                                        "FROM posts ;"
                        );
                PreparedStatement postTagsRelationQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT tag_id, tag_name " +
                                        "FROM post_tag_relation ptr LEFT JOIN tags t on t.tag_id = ptr.fk_tag_id " +
                                        "WHERE ptr.fk_post_id = ?;"
                        );
        ) {
            ResultSet answerToPostTableQuery = postTableQuery.executeQuery();

            while (answerToPostTableQuery.next()) {
                long postId = answerToPostTableQuery.getLong("post_id");

                Post p = new Post();
                p.setId(postId);
                p.setPostContent(answerToPostTableQuery.getString("post_content"));
                p.setPostStatus(PostStatus.valueOf(answerToPostTableQuery.getString("post_status")));
                p.setWriter_id(answerToPostTableQuery.getLong("fk_writer_id"));

                postTagsRelationQuery.setLong(1, postId);
                ResultSet answerToPostTagRelationQuery = postTagsRelationQuery.executeQuery();

                while (answerToPostTagRelationQuery.next()) {
                    Tag t = new Tag();
                    t.setId(answerToPostTagRelationQuery.getLong("tag_id"));
                    t.setTagName(answerToPostTagRelationQuery.getString("tag_name"));
                    p.addTag(t);
                }
                postsToBeStreamed.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postsToBeStreamed.stream();
    }

    @Override
    public Long getFreeId() {
        long idToBeReturned = -1L;
        try (
                PreparedStatement queryToDataBase =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT MAX(post_id) + 1 as free_id FROM posts;"
                        )
        ) {
            ResultSet answerToQuery = queryToDataBase.executeQuery();
            idToBeReturned = answerToQuery.getInt("free_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idToBeReturned;
    }

    @Override
    public Stream<Post> getPostsStreamByWriter(Writer w) {
        List<Post> postsToBeStreamed = new ArrayList<>();
        try (
                PreparedStatement postTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT post_id, post_content, post_status, fk_writer_id " +
                                        "FROM posts " +
                                        "WHERE fk_writer_id = ? ;"
                        );
                PreparedStatement postTagsRelationQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT tag_id, tag_name " +
                                        "FROM post_tag_relation ptr LEFT JOIN tags t on t.tag_id = ptr.fk_tag_id " +
                                        "WHERE ptr.fk_post_id = ?;"
                        );
        ) {
            postTableQuery.setLong(1, w.getId());
            ResultSet answerToPostTableQuery = postTableQuery.executeQuery();

            while (answerToPostTableQuery.next()) {
                long postId = answerToPostTableQuery.getLong("post_id");

                Post p = new Post();
                p.setId(postId);
                p.setPostContent(answerToPostTableQuery.getString("post_content"));
                p.setPostStatus(PostStatus.valueOf(answerToPostTableQuery.getString("post_status")));
                p.setWriter_id(answerToPostTableQuery.getLong("fk_writer_id"));

                postTagsRelationQuery.setLong(1, postId);
                ResultSet answerToPostTagRelationQuery = postTagsRelationQuery.executeQuery();

                Tag t = new Tag();
                while (answerToPostTagRelationQuery.next()) {
                    t.setId(answerToPostTagRelationQuery.getLong("tag_id"));
                    t.setTagName(answerToPostTagRelationQuery.getString("tag_name"));
                    p.addTag(t);
                }
                postsToBeStreamed.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postsToBeStreamed.stream();
    }

    @Override
    public void deleteByStatus(PostStatus ps) {
        try (PreparedStatement deletePostByStatus = PreparedStatementProvider.prepareStatement(
                "DELETE FROM posts WHERE post_status = ? ;"
        )) {
            deletePostByStatus.setString(1, ps.name());
            deletePostByStatus.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTagFromPost(Tag t) {
        long tagId = t.getId();
        try (PreparedStatement deleteTagFromPosts = PreparedStatementProvider.prepareStatement(
                "DELETE FROM post_tag_relation WHERE fk_tag_id = ? ;"
        )) {
            deleteTagFromPosts.setLong(1, tagId);
            deleteTagFromPosts.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

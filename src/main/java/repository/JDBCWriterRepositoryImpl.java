package repository;

import model.Writer;
import util.PreparedStatementProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JDBCWriterRepositoryImpl implements WriterRepository {

    private Writer getWriterFromResultSet(ResultSet resultSet) {
        Writer writerToBeReturned = new Writer();
        try {
            if (resultSet.next()) {
                writerToBeReturned.setId(resultSet.getLong("writer_id"));
                writerToBeReturned.setWriterName(resultSet.getString("writer_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writerToBeReturned;
    }

    private List<Writer> getListOfWritersFromResultSet(ResultSet resultSet) {
        List<Writer> writersListToBeReturned = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Writer writerForList = new Writer();
                writerForList.setId(resultSet.getLong("writer_id"));
                writerForList.setWriterName(resultSet.getString("writer_name"));
                writersListToBeReturned.add(writerForList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writersListToBeReturned;
    }

    @Override
    public void add(Writer w) {

        if (w.getId() != -1L) {
            throw new IllegalArgumentException("Writer id values is " + w.getId() + " , but shall be -1L");
        }

        try (
                PreparedStatement writersTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "INSERT INTO writers (writer_name) VALUE (?);"
                        )
        ) {
            writersTableQuery.setString(1, w.getWriterName());
            writersTableQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean contains(Writer w) {
        return this.containsId(w.getId());
    }

    @Override
    public boolean containsId(Long id) {
        boolean isWriterExist = false;

        try (
                PreparedStatement writersTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT COUNT(writer_name) as count FROM writers " +
                                        "WHERE writer_id = ? ;"
                        );
        ) {
            writersTableQuery.setLong(1, id);
            ResultSet answerToQuery = writersTableQuery.executeQuery();
            if (answerToQuery.next()) {
                isWriterExist = answerToQuery.getInt("count") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isWriterExist;
    }

    @Override
    public Writer getById(Long id) {
        if (id < 1L) {
            throw new IllegalArgumentException("Id shall be positive value");
        }
        Writer writerToBeReturned = new Writer();
        try (
                PreparedStatement writersTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT writer_id, writer_name FROM writers WHERE writer_id = ? ;"
                        );
        ) {
            writersTableQuery.setLong(1,id);
            ResultSet answerToQuery = writersTableQuery.executeQuery();
            writerToBeReturned = this.getWriterFromResultSet(answerToQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writerToBeReturned;
    }

    @Override
    public void update(Writer w) {
        if (w.getId() < 1L) {
            throw new IllegalArgumentException("Id shall be positive value");
        }
        try (
                PreparedStatement writerTableUpdateQuery =
                        PreparedStatementProvider.prepareStatement(
                                "UPDATE writers SET writers.writer_name = ? " +
                                        "WHERE writers.writer_id = ? ;"
                        )
        ) {
            writerTableUpdateQuery.setString(1, w.getWriterName());
            writerTableUpdateQuery.setLong(2, w.getId());
            writerTableUpdateQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Writer w) {
        this.deleteById(w.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id < 1L) {
            throw new IllegalArgumentException("Id shall be positive value");
        }
        try (
                PreparedStatement deleteWriterTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "DELETE FROM writers WHERE writers.writer_id = ?;"
                        )
        ) {
            deleteWriterTableQuery.setLong(1, id);
            deleteWriterTableQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stream<Writer> getObjectsStream() {
        List<Writer> writersToBeStreamed = new ArrayList<>();
        try (
                PreparedStatement writersTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT writer_id,writer_name FROM writers;"
                        )
        ) {
            ResultSet answerToQuery = writersTableQuery.executeQuery();
            writersToBeStreamed = this.getListOfWritersFromResultSet(answerToQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writersToBeStreamed.stream();
    }

    @Override
    public Long getFreeId() {
        long idToBeReturned = -1L;
        try (
                PreparedStatement queryToDataBase = PreparedStatementProvider
                        .prepareStatement("SELECT MAX(writer_id)+1 as freeId " +
                                "FROM writers ;")
        ) {
            ResultSet answerToQuery = queryToDataBase.executeQuery();
            idToBeReturned = answerToQuery.getInt("freeId");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idToBeReturned;
    }

    @Override
    public boolean writerNameContains(String writerName) {
        boolean isWriterNameContains = false;

        try (
                PreparedStatement writerTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT COUNT(writer_id) as count FROM writers " +
                                        "WHERE writer_name = ? ;"
                        )
        ) {
            writerTableQuery.setString(1, writerName);
            ResultSet answerToQuery = writerTableQuery.executeQuery();
            if (answerToQuery.next()) {
                isWriterNameContains = answerToQuery.getInt("count") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isWriterNameContains;
    }

    @Override
    public Writer getByName(String name) {
        Writer writerToBeReturned = new Writer();
        try (
                PreparedStatement writerTableQuery =
                        PreparedStatementProvider.prepareStatement(
                                "SELECT writer_id,writer_name FROM writers " +
                                        "WHERE writer_name = ?;"
                        )
        ) {
            writerTableQuery.setString(1, name);
            ResultSet queryAnswer = writerTableQuery.executeQuery();
            writerToBeReturned = this.getWriterFromResultSet(queryAnswer);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writerToBeReturned;
    }
}

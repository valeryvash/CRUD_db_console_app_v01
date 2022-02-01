package repository;

import java.util.stream.Stream;

public interface GenericRepository<ID, T> {

    void add(T t);

    boolean contains(T t);

    boolean containsId(ID id);

    T getById(ID id);

    void update(T t);

    void delete(T t);

    void deleteById(ID id);

    Stream<T> getObjectsStream();

    ID getFreeId();
}

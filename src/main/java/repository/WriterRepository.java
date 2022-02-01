package repository;

import model.Writer;

import java.util.stream.Stream;

public interface WriterRepository extends GenericRepository<Long, Writer>{

    @Override
    void add(Writer w);

    @Override
    boolean contains(Writer w);

    @Override
    boolean containsId(Long id);

    @Override
    Writer getById(Long id);

    @Override
    void update(Writer w);

    @Override
    void delete(Writer w);

    @Override
    void deleteById(Long id);

    @Override
    Stream<Writer> getObjectsStream();

    @Override
    Long getFreeId();

    boolean writerNameContains(String writerName);

    Writer getByName(String name);

}

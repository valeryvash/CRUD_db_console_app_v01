package service;

import model.Writer;
import repository.WriterRepository;

import java.util.stream.Stream;

public class WriterService {

    private WriterRepository writerRepo;

    public WriterService(WriterRepository writerRepo) {
        this.writerRepo = writerRepo;
    }

    private WriterService() {}

    public void add(Writer w) {
        writerRepo.add(w);
    }

    public boolean contains(Writer w) {
        return writerRepo.contains(w);
    }

    public boolean containsId(Long id) {
        return writerRepo.containsId(id);
    }

    public Writer getById(Long id) {
        return writerRepo.getById(id);
    }

    public void update(Writer w) {
        writerRepo.update(w);
    }

    public void delete(Writer w) {
        writerRepo.delete(w);
    }

    public void deleteById(Long id) {
        writerRepo.deleteById(id);
    }

    public Stream<Writer> getObjectsStream() {
        return writerRepo.getObjectsStream();
    }

    public Long getFreeId() {
        return writerRepo.getFreeId();
    }

    public boolean writerNameContains(String writerName) {
        return writerRepo.writerNameContains(writerName);
    }

    public Writer getByName(String name) {
        return writerRepo.getByName(name);
    }
}

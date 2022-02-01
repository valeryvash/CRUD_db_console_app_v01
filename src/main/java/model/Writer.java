package model;

public class Writer implements Entity<Long> {
    private Long id;
    private String writerName;

    public Writer() {
        this.id = -1L;
        this.writerName = "";
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }
}

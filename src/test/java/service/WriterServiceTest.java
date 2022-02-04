package service;

import model.Entity;
import model.Tag;
import model.Writer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

class WriterServiceTest {

    @Mock
    static WriterService writerService;

    @BeforeAll
    static void beforeAll() {
        writerService = mock(WriterService.class);
    }

    @Test
    void add() {
        doNothing().when(writerService).add(isA(Writer.class));

        Writer testWriter = new Writer();
        Entity entity = new Writer();

        writerService.add(testWriter);
        writerService.add((Writer) entity);

        Assertions.assertThrows(ClassCastException.class,()->
        {
            Entity secret = new Tag();
            writerService.add((Writer) secret);
        });

        verify(writerService,times(1)).add(testWriter);
    }

    @Test
    void contains() {
        Writer trueContainsWriter = new Writer();
        trueContainsWriter.setId(500L);
        Writer falseContainsWriter = new Writer();


        doReturn(true).when(writerService).contains(trueContainsWriter);
        doReturn(false).when(writerService).contains(falseContainsWriter);

        boolean firstCase = writerService.contains(trueContainsWriter);
        boolean secondCase = writerService.contains(falseContainsWriter);

        Assertions.assertTrue(firstCase);
        Assertions.assertFalse(secondCase);
    }

}
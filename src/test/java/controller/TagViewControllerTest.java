package controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class TagViewControllerTest {
    @Mock
    TagViewController tvc = spy(new TagViewController());

    @BeforeEach
    void setUp() throws FileNotFoundException {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void printAllTags() {
        tvc.printAllTags();
    }

    @Test
    void updateTag() throws FileNotFoundException {
        tvc.updateTag();
        System.setIn(new FileInputStream("src/test/resources/TagViewSystemIn.txt"));
    }

    @Test
    void deleteTag() {

    }
}
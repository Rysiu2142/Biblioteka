package main.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ISBNTest {
    @Test
    void validISBN(){
        String isbn = ISBN.generate();
        boolean isValid = Validation.validNumber(isbn);
        assertEquals(true,isValid);
    }
}

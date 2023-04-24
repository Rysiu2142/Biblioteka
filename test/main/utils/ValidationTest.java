package main.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ValidationTest {
    @Test
    void validEmail(){
        String email = "dvd@gmail.com";
        boolean isValid = Validation.validEmail(email);
        assertEquals(true,isValid);
    }
    @Test
    void invalidEmail(){
        String email = "dvd";
        boolean isValid = Validation.validEmail(email);
        assertEquals(false,isValid);
    }
    @Test
    void validPassword(){
        String password = "gftftghy2";
        boolean isValid = Validation.validPassword(password);
        assertEquals(true,isValid);
    }
    @Test
    void invalidPassword(){
        String password = "12345";
        boolean isValid = Validation.validPassword(password);
        assertEquals(false,isValid);
    }
    @Test
    void validNumber(){
        String number = "313131";
        boolean isValid = Validation.validNumber(number);
        assertEquals(true,isValid);
    }
    @Test
    void invalidNumber(){
        String number = "aaaa";
        boolean isValid = Validation.validNumber(number);
        assertEquals(false,isValid);
    }
    @Test
    void validPhoneNumber(){
        String phone = "1234567890";
        boolean isValid = Validation.validPhoneNumber(phone);
        assertEquals(true,isValid);
    }
    @Test
    void invalidPhoneNumber(){
        String phone = "1234";
        boolean isValid = Validation.validPhoneNumber(phone);
        assertEquals(false,isValid);
    }

}

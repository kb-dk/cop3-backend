package dk.kb.cop3.backend.crud.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * User: abwe
 * Date: 4/13/11
 * Time: 11:28 AM
 */
public class ValidatePathParamTest {

    @Test
    public void validateMonth_jan_is_valid_month() {
        assertTrue(ValidatorPathParam.validateMonth("jan"));
    }

    @Test
    public void validateMonth_okt_is_valid_month() {
        assertTrue(ValidatorPathParam.validateMonth("okt"));
    }

    @Test
    public void validateMonth_june_is_invalid_month() {
        assertFalse(ValidatorPathParam.validateMonth("june"));
    }

    @Test
    public void validateMonth_Mumbojumbo_is_invalid_month() {
        assertFalse(ValidatorPathParam.validateMonth("Mumbojumbo"));
    }

    @Test
    public void validateMonth_OKT_is_invalid_month() {
        assertFalse(ValidatorPathParam.validateMonth("OKT"));
    }

    @Test
    public void validateMonth_several_months_are_invalid_input() {
        assertFalse(ValidatorPathParam.validateMonth("jan|feb"));
    }

    @Test
    public void validateYear_1000_is_valid_year() {
        assertTrue(ValidatorPathParam.validateYear(1000));
    }

    @Test
    public void validateYear_1000_as_string_is_valid_year() {
        assertTrue(ValidatorPathParam.validateYear("1000"));
    }

    @Test
    public void validateYear_999_is_invalid_year() {
        assertFalse(ValidatorPathParam.validateYear(999));
    }

    @Test
    public void validateYear_999_as_string_is_invalid_year() {
        assertFalse(ValidatorPathParam.validateYear("999"));
    }

    @Test
    public void validateYear_null_string_is_invalid_year() {
        assertFalse(ValidatorPathParam.validateYear(null));
    }

    @Test
    public void validateYear_empty_string_is_invalid_year() {
        assertFalse(ValidatorPathParam.validateYear(""));
    }

    @Test
    public void validateYear_spaced_string_is_invalid_year() {
        assertFalse(ValidatorPathParam.validateYear("    "));
    }

    @Test
    public void validateYear_not_integer_string_is_invalid_year() {
        assertFalse(ValidatorPathParam.validateYear("Not a year"));
    }

    @Test
    public void validateString_null_string_is_invalid() {
        assertFalse(ValidatorPathParam.validateString(null));
    }

    @Test
    public void validateString_empty_string_is_invalid() {
        assertFalse(ValidatorPathParam.validateString(""));
    }

    @Test
    public void validateString_1_character_string_is_valid() {
        assertTrue(ValidatorPathParam.validateString("a"));
    }

    @Test
    public void validateString_short_string_is_valid() {
        assertTrue(ValidatorPathParam.validateString("luftfoto"));
    }

    @Test
    public void validateString_255_character_string_is_valid() {
        assertTrue(ValidatorPathParam.validateString("kCkhDvHxbDCqjrJSmw6af0TCmYKYCGPVlO0ZzLG8dNndvhD5qdHENhTXgZEdPgxrJwKWRi6oEwSqATaBEucv01YEZZY77nTr7qB8Uh1NDWXLo5WpokH4NsbBD0XeoS2YAX8FOZwscessHmbbkodVPx8ziWlNIWRCLmywMe6Ac1cklM4vv7kQ35OX9YSpwRjqnqVOuPhR5MxI3LeFgQz59Uc0Km2skoKN6qsI9O0lAnXtwdkX2HVDfppZystvWEr"));
    }

    @Test
    public void validateString_256_character_string_is_invalid() {
        assertFalse(ValidatorPathParam.validateString("kCkhDvHxbDCqjrJSmw6af0TCmYKYCGPVlO0ZzLG8dNndvhD5qdHENhTXgZEdPgxrJwKWRi6oEwSqATaBEucv01YEZZY77nTr7qB8Uh1NDWXLo5WpokH4NsbBD0XeoS2YAX8FOZwscessHmbbkodVPx8ziWlNIWRCLmywMe6Ac1cklM4vv7kQ35OX9YSpwRjqnqVOuPhR5MxI3LeFgQz59Uc0Km2skoKN6qsI9O0lAnXtwdkX2HVDfppZystvWErQ"));
    }


    @Test
    public void validateStrings_all_strings_are_valid() {
        assertTrue(ValidatorPathParam.validateStrings("manus", "fart", "letters"));
    }

    @Test
    public void validateStrings_one_string_is_invalid() {
        assertFalse(ValidatorPathParam.validateStrings("", "fart", "letters"));
    }

    @Test
    public void validateStrings_all_strings_are_invalid() {
        assertFalse(ValidatorPathParam.validateStrings("", null, ""));
    }
}

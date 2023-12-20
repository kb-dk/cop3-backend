package dk.kb.cop3.backend.crud.util;

import dk.kb.cop3.backend.constants.Months;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: abwe
 * Date: 4/12/11
 * Time: 3:25 PM
 */
public class ValidatorPathParam {
    private static final Logger logger = LoggerFactory.getLogger(ValidatorPathParam.class);

    public static boolean validateMonth(String aMonth) {
        return aMonth != null && !aMonth.isEmpty() && aMonth.length() == 3 &&
                !aMonth.contains("|") && Months.MONTHS.contains(aMonth);
    }

    public static boolean validateYear(int year) {
        return year >= 1000;
    }

    public static boolean validateYear(String year) {
        return validateYear(getIntegerValue(year));
    }

    private static int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * Validator method for editions, medium and collections. Validates that the string has more than one character, less than 256.
     *
     * @param anyString a string
     * @return
     */
    public static boolean validateString(String anyString) {
        return anyString != null && !anyString.isEmpty() && anyString.length() < 256;
    }

    /**
     * wrapper method, capable of handling a variable list of strings
     *
     * @param strings
     * @return true if all strings validates, or false if one or more strings do not validate
     */
    public static boolean validateStrings(String... strings) {
        for (String aString : strings) {
            if (!validateString(aString)) {
                return false;
            }
        }
        return true;
    }
}

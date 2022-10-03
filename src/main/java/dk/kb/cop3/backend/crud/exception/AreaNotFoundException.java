package dk.kb.cop3.backend.crud.exception;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 09/10/13
 *         Time: 16:55
 */
public class AreaNotFoundException extends Exception {

    public AreaNotFoundException() {
        super("Area not found!");
    }
}

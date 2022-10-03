package dk.kb.cop3.backend.constants;


/**
 * Created by IntelliJ IDEA.
 * User: jac
 * Date: 28-04-11
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class Types {


    public static int getTypeByName(String name) {
        if (name.equals("Skråfoto")) {
            return 1;
        } else if (name.equals("Lodfoto")) {
            return 2;
        } else if (name.equals("Protokolside")) {
            return 3;
        } else if (name.equals("Tema")) {
            return 4;
        } else if (name.equals("Billede")) {
            return 5;
        } else if (name.equals("Unknown")) {
            return 6;
        } else if (name.equals("Småtryk")) {
            return 7;
        } else {
            return 6;
        }
    }

    public enum VoresTyper {
        Skråfoto {
            public String toString() {
                return "Skråfoto";
            }
        },

        Lodfoto {
            public String toString() {
                return "Lodfoto";
            }
        },

        Protokol {
            public String toString() {
                return "Protokolside";
            }
        },
        Tema {
            public String toString() {
                return "Tema";
            }
        }

    }
}

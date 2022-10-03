package dk.kb.cop3.backend.constants;

/**
 * Helper Class containing ENUMS and hardcoded values for all the areas defined in DSFL
 * User: abwe
 * Date: 09-10-13
 * Time: 11:00
 */
public class Areas {

    /**
     * Retrieve an int id for an area
     *
     * @param name of the area
     * @return an int value.
     */
    public static int getAreaByName(String name) {
        if (name.equalsIgnoreCase("Fyn")) {
            return 1;
        } else if (name.equalsIgnoreCase("Bornholm")) {
            return 2;
        } else if (name.equalsIgnoreCase("Hovedstaden")) {
            return 3;
        } else if (name.equalsIgnoreCase("Kattegat")) {
            return 4;
        } else if (name.equalsIgnoreCase("Lolland-Falster")) {
            return 5;
        } else if (name.equalsIgnoreCase("Midtjylland")) {
            return 6;
        } else if (name.equalsIgnoreCase("Nordjylland")) {
            return 7;
        } else if (name.equalsIgnoreCase("Sjælland")) {
            return 8;
        } else if (name.equalsIgnoreCase("Sønderjylland") || name.equalsIgnoreCase("Sydjylland")) {
            // Keep 'sønderjylland' for legacy purposes
            return 9;
        } else {
            return 0;
        }
    }

    /**
     * Retrieve an enum value for a given area from a string. Not case sensitve.
     *
     * @param name name of the area
     * @return DSFLAreas enum value
     */
    public static DSFLAreas getAreaEnumByName(String name) {
        if (name.equalsIgnoreCase("Fyn")) {  // 1
            return DSFLAreas.Fyn;
        } else if (name.equalsIgnoreCase("Bornholm")) { // 2
            return DSFLAreas.Bornholm;
        } else if (name.equalsIgnoreCase("Hovedstaden")) { // 3
            return DSFLAreas.Hovedstaden;
        } else if (name.equalsIgnoreCase("Kattegat")) {      //4
            return DSFLAreas.Kattegat;
        } else if (name.equalsIgnoreCase("Lolland-Falster")) {    //5
            return DSFLAreas.LollandFalster;
        } else if (name.equalsIgnoreCase("Midtjylland")) {  //6
            return DSFLAreas.Midtjylland;
        } else if (name.equalsIgnoreCase("Nordjylland")) {   //7
            return DSFLAreas.Nordjylland;
        } else if (name.equalsIgnoreCase("Sjælland")) {   //8
            return DSFLAreas.Sjælland;
        } else if (name.equalsIgnoreCase("Sønderjylland") || name.equalsIgnoreCase("Sydjylland")) {        //9
            return DSFLAreas.Sønderjylland;
        } else {
            return DSFLAreas.Danmark;
        }
    }

    public static String fromEnumToRelevantDBUserFieldName(DSFLAreas anArea) {
        switch (anArea) {
            case Danmark:
                return "userScore";
            case Fyn:                  //1
                return "userScore1";
            case Bornholm:            //2
                return "userScore2";
            case Hovedstaden:        // 3
                return "userScore3";
            case Kattegat:           // 4
                return "userScore4";
            case LollandFalster:     // 5
                return "userScore5";
            case Midtjylland:       //6
                return "userScore6";
            case Nordjylland:       // 7
                return "userScore7";
            case Sjælland:           // 8
                return "userScore8";
            case Sønderjylland:      // p
                return "userScore9";
            default:
                return "userScore";
        }
    }



}

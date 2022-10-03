package dk.kb.cop3.backend.constants;

/**
 * Constants for the datacontroller such as:
 * - exporter root
 * - items dir
 * - master dif
 * - etc.
 */
public final class DatacontrollerConstants {
    /**
     * The root where Cumulus exporter dumps raw.xml files
     * TODO: read from configuration or command line
     */

    public final static int CONN_TIMEOUT = 120000;

    // The const for the Luftfoto l-series
    public final static String LUFTFOTO_L_CATALOG = "Luftfoto_OM";
    public final static String LUFTFOTO_L_ROOT_NODE = "205";

    // The const for the Luftfoto h-series
    public final static String LUFTFOTO_H_CATALOG = "Luftfoto_OM";
    public final static String LUFTFOTO_H_ROOT_NODE = "206";

    public final static String LUFTFOTO_NORM_RULE = "billeder";

    // The const for the Letters edition
    public final static String LETTERS_CATALOG = "Letters_OM";
    public final static String LETTERS_ROOT_NODE = "4";
    public final static String LETTERS_NORM_RULE = "brever";

    // The const that applies to all editions
    public final static String ITEMS_DIR = "item_records";
    public final static String MASTER_DIR = "master_records";
}

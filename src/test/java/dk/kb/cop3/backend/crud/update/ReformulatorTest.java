package dk.kb.cop3.backend.crud.update;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/*
Test af reformulators xslt
Opdaterer specifikke felter i mods dokument
 */
public class ReformulatorTest {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ReformulatorTest.class);
    java.lang.String text;

    @BeforeClass
    public static void initTest() {
    }

    @Test
    public void runTest() {
	this.text = this.getDocument();
	Reformulator rf = new Reformulator(text);
    // TODO: tilføj manglende felter
	rf.changeField("title","This is the new title");
    rf.changeField("note","This is a note");
//    rf.changeField("name","this is the new name");
    rf.changeField("building","this is the new building");
    rf.changeField("location","this is the new location");
    rf.changeField("creator","A new creator");
    rf.changeField("dateCreated","2013");
	System.out.println(rf.commitChanges());
    }


    @AfterClass
    public static void close() {
    }

    public String getDocument() {
        return ReformulatorTest.document;
    }

 /*
    public String getDocument() {
        try {
            java.io.File file =
		new java.io.File("/home/slu/projects/cop3/misc/mods-example/judaica_manuscript.xml");

            logger.debug("Reading from file " + file.getName());
            java.lang.StringBuilder text = new java.lang.StringBuilder();
            String NL = System.getProperty("line.separator");
            java.util.Scanner scanner = new java.util.Scanner(new java.io.FileInputStream(file));
            try {
                while (scanner.hasNextLine()) {
                    text.append(scanner.nextLine() + NL);
                }
            } finally {
                scanner.close();
            }
            return text.toString();
        } catch (java.io.FileNotFoundException noFile) {
            logger.error(noFile.getMessage());
            return "";
        }
    }
*/

    //TODO: udskift eksempel med luftfoto eksempel
    private static final String document ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "\n" +
            "<md:mods xmlns:md=\"http://www.loc.gov/mods/v3\" \n" +
            "\t xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
            "\t xmlns:t=\"http://www.tei-c.org/ns/1.0\" \n" +
            "\t xmlns:xlink=\"http://www.w3.org/1999/xlink\" \n" +
            "\t xsi:schemaLocation=\"http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-3.xsd\">\n" +
            "  <md:identifier type=\"uri\">/manus/judsam/2010/maj/jsmss/da/object13800/</md:identifier>\n" +
            "  <md:recordInfo>\n" +
            "    <md:languageOfCataloging>\n" +
            "      <md:languageTerm authority=\"rfc4646\">en</md:languageTerm>\n" +
            "    </md:languageOfCataloging>\n" +
            "    <md:recordIdentifier>13800</md:recordIdentifier>\n" +
            "    <md:recordCreationDate encoding=\"w3cdtf\">2009-02-19</md:recordCreationDate>\n" +
            "    <md:recordChangeDate encoding=\"w3cdtf\">2010-05-17</md:recordChangeDate>\n" +
            "  </md:recordInfo>\n" +
            "  <md:titleInfo xml:lang=\"he\">\n" +
            "    <md:title>סדר ברכת המזון</md:title>\n" +
            "  </md:titleInfo>\n" +
            "  <md:titleInfo xml:lang=\"he\">\n" +
            "    <md:subTitle>עם תיקון קריאת שמע</md:subTitle>\n" +
            "  </md:titleInfo>\n" +
            "  <md:titleInfo xml:lang=\"en\" type=\"transcribed\">\n" +
            "    <md:title>Seder Birkat ha-mazon</md:title>\n" +
            "  </md:titleInfo>\n" +
            "  <md:name xml:lang=\"en\" type=\"personal\">\n" +
            "    <md:namePart>Unknown; Shmuel ben Zvi Dreznitz has been suggested by earlier research.</md:namePart>\n" +
            "    <md:role>\n" +
            "      <md:roleTerm type=\"code\">src</md:roleTerm>\n" +
            "    </md:role>\n" +
            "  </md:name>\n" +
            "  <md:name xml:lang=\"en\" type=\"personal\">\n" +
            "    <md:namePart>Name of the creator</md:namePart>\n" +
            "    <md:role>\n" +
            "      <md:roleTerm type=\"text\">creator</md:roleTerm>\n" +
            "    </md:role>\n" +
            "  </md:name>\n" +
            "  <md:note xml:lang=\"en\">Known locally as \"Den lille bønnebog\" (\"The little prayer book\").</md:note>\n" +
            "  <md:note xml:lang=\"en\">For a short introduction, see: http://www.kb.dk/da/nb/samling/js/ch32.html (Danish) or http://www.kb.dk/en/nb/samling/js/ch32.html (English).</md:note>\n" +
            "  <md:note type=\"content\" xml:lang=\"en\">Grace after meals and other benedictions</md:note>\n" +
            "  <md:subject>\n" +
            "    <md:topic xml:lang=\"en\">Liturgy</md:topic>\n" +
            "  </md:subject>\n" +
            "  <md:subject>\n" +
            "    <md:topic xlink:href=\"#750\">Tjekkiet</md:topic>\n" +
            "  </md:subject>\n" +
            "  <md:subject>\n" +
            "    <md:topic xlink:href=\"#754\">Cod. Heb. 032</md:topic>\n" +
            "  </md:subject>\n" +
            "  <md:subject>\n" +
            "    <md:topic xlink:href=\"#756\">Hebraisk</md:topic>\n" +
            "  </md:subject>\n" +
            "  <md:subject>\n" +
            "    <md:topic xlink:href=\"#757\">Jiddisch</md:topic>\n" +
            "  </md:subject>\n" +
            "\n" +
            "  <md:extension xlink:href=\"#750\">\n" +
            "    <h:div xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:exsl=\"http://exslt.org/common\" xlink:href=\"#750\">\n" +
            "      <h:a xml:lang=\"en\" href=\"/editions/any/2009/jul/editions/en/\">Home</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"/editions/any/2009/jul/editions/da/\">Hjem</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject748/en/\">Judaica Collection: Manuscripts</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"../../subject748/da/\">Judaistisk Samling: Håndskrifter</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject749/en/\">Country of origin</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"../../subject749/da/\">Ophavsland</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject750/en/\">Czechia</h:a>\n" +
            "    <h:a xml:lang=\"da\" href=\"../../subject750/da/\">Tjekkiet</h:a></h:div>\n" +
            "  </md:extension>\n" +
            "  <md:extension xlink:href=\"#754\">\n" +
            "    <h:div xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:exsl=\"http://exslt.org/common\" xlink:href=\"#754\">\n" +
            "      <h:a xml:lang=\"en\" href=\"/editions/any/2009/jul/editions/en/\">Home</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"/editions/any/2009/jul/editions/da/\">Hjem</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject748/en/\">Judaica Collection: Manuscripts</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"../../subject748/da/\">Judaistisk Samling: Håndskrifter</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject751/en/\">Opstillingssignatur</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"../../subject751/da/\">Opstillingssignatur</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject752/en/\">Cod. Heb. 001-046</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"../../subject752/da/\">Cod. Heb. 001-046</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject753/en/\">Cod. Heb. 031-040</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"../../subject753/da/\">Cod. Heb. 031-040</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject754/en/\">Cod. Heb. 032</h:a>\n" +
            "    <h:a xml:lang=\"da\" href=\"../../subject754/da/\">Cod. Heb. 032</h:a></h:div>\n" +
            "  </md:extension>\n" +
            "  <md:extension xlink:href=\"#756\">\n" +
            "    <h:div xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:exsl=\"http://exslt.org/common\" xlink:href=\"#756\">\n" +
            "      <h:a xml:lang=\"en\" href=\"/editions/any/2009/jul/editions/en/\">Home</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"/editions/any/2009/jul/editions/da/\">Hjem</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject748/en/\">Judaica Collection: Manuscripts</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"../../subject748/da/\">Judaistisk Samling: Håndskrifter</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject755/en/\">Language</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"../../subject755/da/\">Sprog</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject756/en/\">Hebrew</h:a>\n" +
            "    <h:a xml:lang=\"da\" href=\"../../subject756/da/\">Hebraisk</h:a></h:div>\n" +
            "  </md:extension>\n" +
            "  <md:extension xlink:href=\"#757\">\n" +
            "    <h:div xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:exsl=\"http://exslt.org/common\" xlink:href=\"#757\">\n" +
            "      <h:a xml:lang=\"en\" href=\"/editions/any/2009/jul/editions/en/\">Home</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"/editions/any/2009/jul/editions/da/\">Hjem</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject748/en/\">Judaica Collection: Manuscripts</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"../../subject748/da/\">Judaistisk Samling: Håndskrifter</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject755/en/\">Language</h:a>\n" +
            "      <h:a xml:lang=\"da\" href=\"../../subject755/da/\">Sprog</h:a> / \n" +
            "      <h:a xml:lang=\"en\" href=\"../../subject757/en/\">Yiddish</h:a>\n" +
            "    <h:a xml:lang=\"da\" href=\"../../subject757/da/\">Jiddisch</h:a></h:div>\n" +
            "  </md:extension>\n" +
            "  <md:identifier type=\"uri\" displayLabel=\"image\">http://www.kb.dk/imageService/online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_60.jpg</md:identifier>\n" +
            "  <md:identifier type=\"uri\" displayLabel=\"thumbnail\">http://www.kb.dk/imageService/w150/h150/online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_60.jpg</md:identifier>\n" +
            "  <md:physicalDescription displayLabel=\"Medium\">\n" +
            "    <md:note type=\"additional physical form\">Parchment</md:note>\n" +
            "  </md:physicalDescription>\n" +
            "  <md:physicalDescription displayLabel=\"Extent\">\n" +
            "    <md:extent>iv, 20, iv fols</md:extent>\n" +
            "  </md:physicalDescription>\n" +
            "  <md:physicalDescription displayLabel=\"Size\">\n" +
            "    <md:extent>116 x 80 mm (average)</md:extent>\n" +
            "  </md:physicalDescription>\n" +
            "  <!--This is more or less a kind of processing\n" +
            "      instruction. Here we tell the rendering software whether we\n" +
            "      should browse the images LTR or RTL-->\n" +
            "  <md:physicalDescription>\n" +
            "    <md:note type=\"pageOrientation\">RTL</md:note>\n" +
            "  </md:physicalDescription>\n" +
            "  <md:typeOfResource manuscript=\"yes\">\n" +
            "    text\n" +
            "  </md:typeOfResource>\n" +
            "  <md:language>\n" +
            "    <md:languageTerm authority=\"rfc4646\" type=\"code\">he</md:languageTerm>\n" +
            "    <md:languageTerm authority=\"rfc4646\" type=\"code\">yi</md:languageTerm>\n" +
            "  </md:language>\n" +
            "  <md:note displayLabel=\"Script\" \n" +
            "\t   type=\"additional physical form\" xml:lang=\"en\">Square Ashkenazi script</md:note>\n" +
            "  <md:note displayLabel=\"Script: detail\" \n" +
            "\t   type=\"additional physical form\" \n" +
            "\t   xml:lang=\"en\">Yiddish instructions in vaybertaytsh/cursive script</md:note>\n" +
            "  <md:originInfo>\n" +
            "    <md:dateCreated t:notBefore=\"1727-01-01\" t:notAfter=\"1728-12-31\" encoding=\"w3cdtf\">1728</md:dateCreated>\n" +
            "    <md:dateCreated encoding=\"w3cdtf\">1951</md:dateCreated>"+
            "    <md:place>\n" +
            "      <md:placeTerm type=\"text\">Nikolsburg</md:placeTerm>\n" +
            "    </md:place>\n" +
            "    <md:place>\n" +
            "      <md:placeTerm type=\"code\" authority=\"iso3166\">cz</md:placeTerm>\n" +
            "    </md:place>\n" +
            "  </md:originInfo>\n" +
            "  <md:relatedItem displayLabel=\"Collection\" type=\"host\">\n" +
            "    <md:titleInfo xml:lang=\"en\">\n" +
            "      <md:title>The Judaica Collection</md:title>\n" +
            "    </md:titleInfo>\n" +
            "    <md:typeOfResource xml:lang=\"en\" collection=\"yes\">\n" +
            "      mixed material\n" +
            "    </md:typeOfResource>\n" +
            "  </md:relatedItem>\n" +
            "  <md:location>\n" +
            "    <md:physicalLocation displayLabel=\"Shelf Mark\">Cod. Heb. 32</md:physicalLocation>\n" +
            "    <md:physicalLocation displayLabel=\"Shelf Mark\" transliteration=\"rex\">Cod. Hebr. 32</md:physicalLocation>\n" +
            "  </md:location>\n" +
            "  <md:note displayLabel=\"Selected references\"\n" +
            "\t   type=\"citation/reference\">Latest facsimile edition: Grace after meals and\n" +
            "  other benedictions. Facsimile of Cod. Hebr. XXXII in the Royal Library,\n" +
            "  Copenhagen. Introduction by Iris Fishof. København: Forlaget Old\n" +
            "  Manuscripts, 1983. ISBN 87-88599-00-9</md:note>\n" +
            "\n" +
            "  <md:relatedItem type=\"constituent\">\n" +
            "    <md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_60.tif</md:identifier>\n" +
            "    <md:titleInfo xml:lang=\"he\">\n" +
            "      <md:title>סדר ברכת המזון</md:title>\n" +
            "    </md:titleInfo>\n" +
            "    <md:relatedItem type=\"constituent\">\n" +
            "      <md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_59.tif</md:identifier>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_58.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_57.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_56.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_55.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_54.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_53.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_52.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_51.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_50.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_49.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_48.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_47.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "    </md:relatedItem>\n" +
            "    <md:relatedItem type=\"constituent\">\n" +
            "      <md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_46.tif</md:identifier>\n" +
            "      <md:titleInfo xml:lang=\"he\">\n" +
            "\t<md:title>ברכת המזון</md:title>\n" +
            "      </md:titleInfo>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_45.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_44.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_43.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_42.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_41.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_40.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_39.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_38.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_37.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_36.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "    </md:relatedItem>\n" +
            "    <md:relatedItem type=\"constituent\">\n" +
            "      <md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_35.tif</md:identifier>\n" +
            "      <md:titleInfo xml:lang=\"he\">\n" +
            "\t<md:title>ברכת הנהנין</md:title>\n" +
            "      </md:titleInfo>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_34.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_33.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_32.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_31.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_30.tif</md:identifier>\n" +
            "        <md:relatedItem type=\"constituent\"> \n" +
            "\t  <md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_29.tif</md:identifier>\n" +
            "\t</md:relatedItem>\n" +
            "        <md:relatedItem type=\"constituent\">\n" +
            "\t  <md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_28.tif</md:identifier>\n" +
            "\t</md:relatedItem>\n" +
            "\t<md:relatedItem type=\"constituent\">\n" +
            "\t  <md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_27.tif</md:identifier>\n" +
            "\t</md:relatedItem>\n" +
            "      </md:relatedItem>\n" +
            "    </md:relatedItem>\n" +
            "    <md:relatedItem type=\"constituent\">\n" +
            "      <md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_26.tif</md:identifier>\n" +
            "      <md:titleInfo xml:lang=\"he\">\n" +
            "\t<md:title>סדר קריאת שמע</md:title>\n" +
            "      </md:titleInfo>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_25.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_24.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_23.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_22.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_21.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_20.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_19.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_18.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_17.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_16.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_15.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_14.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_13.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_12.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_11.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_10.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_09.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_08.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_07.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_06.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_05.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_04.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_03.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\">\n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_02.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "      <md:relatedItem type=\"constituent\"> \n" +
            "\t<md:identifier type=\"uri\">online_master_arkiv_3/non-archival/OJA/codheb32/codheb32_01.tif</md:identifier>\n" +
            "      </md:relatedItem>\n" +
            "    </md:relatedItem>\n" +
            "  </md:relatedItem>\n" +
            "</md:mods>";

}

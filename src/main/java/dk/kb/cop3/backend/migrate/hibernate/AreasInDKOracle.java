package dk.kb.cop3.backend.migrate.hibernate;

import oracle.spatial.geometry.JGeometry;

import java.io.Serializable;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 09/10/13
 *         Time: 17:06
 */
public class AreasInDKOracle implements Serializable {

    private int areaID;
    private String nameOfArea;
    private JGeometry polygonCol;

    public int getAreaID() {
        return areaID;
    }

    public void setAreaID(int areaID) {
        this.areaID = areaID;
    }

    public String getNameOfArea() {
        return nameOfArea;
    }

    public void setNameOfArea(String nameOfArea) {
        this.nameOfArea = nameOfArea;
    }

    public JGeometry getPolygonCol() {
        return polygonCol;
    }

    public void setPolygonCol(JGeometry polygonCol) {
        this.polygonCol = polygonCol;
    }


}

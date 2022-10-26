package dk.kb.cop3.backend.migrate.hibernate;

import oracle.spatial.geometry.JGeometry;
import dk.kb.cop3.backend.crud.database.type.JGeometryType;

import java.io.Serializable;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 09/10/13
 *         Time: 17:06
 */
public class AreasInDkOracle implements Serializable {

    private int areaID;
    private String nameOfArea;
    private JGeometryType polygonCol;

    public int getAreaId() {
        return areaID;
    }

    public void setAreaId(int areaID) {
        this.areaID = areaID;
    }

    public String getNameOfArea() {
        return nameOfArea;
    }

    public void setNameOfArea(String nameOfArea) {
        this.nameOfArea = nameOfArea;
    }

    public JGeometryType getPolygonCol() {
        return polygonCol;
    }

    public void setPolygonCol(JGeometryType polygonCol) {
        this.polygonCol = polygonCol;
    }


}

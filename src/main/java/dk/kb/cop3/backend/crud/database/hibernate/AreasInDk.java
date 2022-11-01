package dk.kb.cop3.backend.crud.database.hibernate;

import org.locationtech.jts.geom.Polygon;

import java.io.Serializable;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 09/10/13
 *         Time: 17:06
 */
public class AreasInDk implements Serializable {

    private String  areaID;
    private String  nameOfArea;
    private Polygon polygonCol;

    public String getAreaId() {
        return areaID;
    }

    public void setAreaId(String areaID) {
        this.areaID = areaID;
    }

    public String getNameOfArea() {
        return nameOfArea;
    }

    public void setNameOfArea(String nameOfArea) {
        this.nameOfArea = nameOfArea;
    }

    public Polygon getPolygonCol() {
        return polygonCol;
    }

    public void setPolygonCol(Polygon polygonCol) {
        this.polygonCol = polygonCol;
    }


}

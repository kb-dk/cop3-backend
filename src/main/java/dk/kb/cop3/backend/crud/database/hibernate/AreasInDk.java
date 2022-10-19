package dk.kb.cop3.backend.crud.database.hibernate;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;

import java.io.Serializable;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 09/10/13
 *         Time: 17:06
 */
public class AreasInDk implements Serializable {

    private String areaID;
    private String nameOfArea;
    private JGeometry polygonCol;

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

    public Geometry getPolygonCol() {
        return polygonCol;
    }

    public void setPolygonCol(Geometry polygonCol) {
        this.polygonCol = polygonCol;
    }


}

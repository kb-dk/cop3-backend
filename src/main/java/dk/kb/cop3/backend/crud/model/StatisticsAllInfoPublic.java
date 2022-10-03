package dk.kb.cop3.backend.crud.model;

/**
 * @author: Andreas B. Westh
 * Date: 8/27/12
 * Time: 11:23 AM
 */
public class StatisticsAllInfoPublic {
    public String objectID;
    public String areaID;
    public String areaName;
    public String categoryID;
    public String categoryName;
    public int noOfCobjects;
    public int noOfCorrectCobjects;
    public String percentage;


    public String getAreaID() {
        return areaID;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getNoOfCobjects() {
        return noOfCobjects;
    }

    public void setNoOfCobjects(int noOfCobjects) {
        this.noOfCobjects = noOfCobjects;
    }

    public int getNoOfCorrectCobjects() {
        return noOfCorrectCobjects;
    }

    public void setNoOfCorrectCobjects(int noOfCorrectCobjects) {
        this.noOfCorrectCobjects = noOfCorrectCobjects;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}

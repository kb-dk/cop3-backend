package dk.kb.cop3.backend.crud.model;

/**
 * @author: Andreas B. Westh
 * Date: 8/27/12
 * Time: 11:23 AM
 */
public class StatisticsForAnEditionPublic {

    public String editionID;
    public String categoryID;
    public int noOfCobjects;
    public int noOfCobjectsPlacedCorrect;
    public String noOfCobjectsPlacedCorrectPercentage;

    public int noOfCobjectsInCategory;
    public int noOfCobjectsInCategoryCorrect;
    public String noOfCobjectsPlacedCategoryCorrectPercentage;


    public String getEditionID() {
        return editionID;
    }

    public void setEditionID(String editionID) {
        this.editionID = editionID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public int getNoOfCobjects() {
        return noOfCobjects;
    }

    public void setNoOfCobjects(int noOfCobjects) {
        this.noOfCobjects = noOfCobjects;
    }

    public int getNoOfCobjectsPlacedCorrect() {
        return noOfCobjectsPlacedCorrect;
    }

    public void setNoOfCobjectsPlacedCorrect(int noOfCobjectsPlacedCorrect) {
        this.noOfCobjectsPlacedCorrect = noOfCobjectsPlacedCorrect;
    }


    public String getNoOfCobjectsPlacedCorrectPercentage() {
        return noOfCobjectsPlacedCorrectPercentage;
    }

    public void setNoOfCobjectsPlacedCorrectPercentage(String noOfCobjectsPlacedCorrectPercentage) {
        this.noOfCobjectsPlacedCorrectPercentage = noOfCobjectsPlacedCorrectPercentage;
    }

    public int getNoOfCobjectsInCategory() {
        return noOfCobjectsInCategory;
    }

    public void setNoOfCobjectsInCategory(int noOfCobjectsInCategory) {
        this.noOfCobjectsInCategory = noOfCobjectsInCategory;
    }

    public int getNoOfCobjectsInCategoryCorrect() {
        return noOfCobjectsInCategoryCorrect;
    }

    public void setNoOfCobjectsInCategoryCorrect(int noOfCobjectsInCategoryCorrect) {
        this.noOfCobjectsInCategoryCorrect = noOfCobjectsInCategoryCorrect;
    }

    public String getNoOfCobjectsPlacedCategoryCorrectPercentage() {
        return noOfCobjectsPlacedCategoryCorrectPercentage;
    }

    public void setNoOfCobjectsPlacedCategoryCorrectPercentage(String noOfCobjectsPlacedCategoryCorrectPercentage) {
        this.noOfCobjectsPlacedCategoryCorrectPercentage = noOfCobjectsPlacedCategoryCorrectPercentage;
    }
}

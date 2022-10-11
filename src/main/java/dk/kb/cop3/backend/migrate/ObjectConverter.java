package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.crud.database.type.JGeometryType;
import dk.kb.cop3.backend.crud.database.type.Point;
import dk.kb.cop3.backend.migrate.hibernate.CategoryOracle;
import dk.kb.cop3.backend.migrate.hibernate.CommentOracle;
import dk.kb.cop3.backend.migrate.hibernate.EditionOracle;
import dk.kb.cop3.backend.migrate.hibernate.ObjectOracle;
import dk.kb.cop3.backend.migrate.hibernate.TypeOracle;

import java.util.HashSet;

public class ObjectConverter {

    public static Edition convertEdition(EditionOracle editionOracle) {
        Edition edition = new Edition();
            edition.setId(editionOracle.getId());
            edition.setName(editionOracle.getName());
            edition.setNameEn(editionOracle.getNameEn());
            edition.setUrlName(editionOracle.getUrlName());
            edition.setUrlMatrialType(editionOracle.getUrlMatrialType());
            edition.setUrlPubYear(editionOracle.getUrlPubYear());
            edition.setUrlPubMonth(editionOracle.getUrlPubMonth());
            edition.setUrlCollection(editionOracle.getUrlCollection());
            edition.setCumulusCatalog(editionOracle.getCumulusCatalog());
            edition.setCumulusTopCatagory(editionOracle.getCumulusTopCatagory());
            edition.setNormalisationrule(editionOracle.getNormalisationrule());
            edition.setStatus(editionOracle.getStatus());
            edition.setUiLanguage(editionOracle.getUiLanguage());
            edition.setUiSort(editionOracle.getUiSort());
            edition.setUiShow(editionOracle.getUiShow());
            edition.setOpml(editionOracle.getOpml());
            edition.setDescription(editionOracle.getDescription());
            edition.setCollectionDa(editionOracle.getCollectionDa());
            edition.setCollectionEn(editionOracle.getCollectionEn());
            edition.setDepartmentDa(editionOracle.getDepartmentDa());
            edition.setDepartmentEn(editionOracle.getDepartmentEn());
            edition.setContactEmail(editionOracle.getContactEmail());
            edition.setObjects(new HashSet<>());
            edition.setVisiblePublic(editionOracle.getVisiblePublic());
            edition.setLastModified(editionOracle.getLastModified());
            return edition;
    }

    public static Category convertCategory(CategoryOracle categoryOracle) {
        Category category = new Category();
        category.setId(categoryOracle.getId());
        category.setCategoryText(categoryOracle.getCategoryText());
        return category;
    }

    public static Type convertType(TypeOracle oraType) {
        Type type = new Type();
        type.setId(oraType.getId());
        type.setTypeText(oraType.getTypeText());
        return type;
    }

    public static Object convertObject(ObjectOracle oraObject) {
        Object object = new Object();
        object.setId(oraObject.getId());
        object.setEdition(convertEdition(oraObject.getEdition()));
        object.setMods(oraObject.getMods());
        object.setLastModified(oraObject.getLastModified());
        object.setDeleted(oraObject.getDeleted()==0x00?'n': oraObject.getDeleted());
        object.setLastModifiedBy(oraObject.getLastModifiedBy());
        object.setType(convertType(oraObject.getType()));
        object.setObjVersion(oraObject.getObjVersion());
        object.setPoint(convertPoint(oraObject.getPoint()));
        object.setTitle(oraObject.getTitle());
        object.setCreator(oraObject.getCreator());
        object.setRandomNumber(oraObject.getRandomNumber());
        object.setInterestingess(oraObject.getInterestingess());
        object.setPerson(oraObject.getPerson());
        object.setBuilding(oraObject.getBuilding());
        object.setLocation(oraObject.getLocation());
        object.setNotBefore(oraObject.getNotBefore());
        object.setNotAfter(oraObject.getNotAfter());
        object.setCorrectness(oraObject.getCorrectness());
        object.setLikes(oraObject.getLikes());
        object.setBookmark(oraObject.getBookmark());
        return object;
    }

    private static Point convertPoint(JGeometryType point) {
        if (point == null) {
            return null;
        }
        return new Point(point.getPoint()[0],point.getPoint()[1]);
    }

}

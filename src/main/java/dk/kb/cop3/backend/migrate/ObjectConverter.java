package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.migrate.hibernate.EditionOracle;

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

}

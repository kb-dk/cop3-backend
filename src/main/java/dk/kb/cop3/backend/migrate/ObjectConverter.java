package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.crud.database.hibernate.*;

import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.database.type.JGeometryType;
import dk.kb.cop3.backend.migrate.hibernate.*;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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


    public static User convertUser(UserOracle oraUser) {
        User user = new User();
        user.setPid(oraUser.getPid());
        user.setId(oraUser.getId());
        user.setGivenName(oraUser.getGivenName());
        user.setSurName(oraUser.getSurName());
        user.setCommonName(oraUser.getCommonName());
        user.setRoleId(oraUser.getRoleId());
        UserRole userRole = createUserRole(oraUser);
        user.setRole(userRole);
        user.setEmail(oraUser.getEmail());
        user.setUserScore(oraUser.getUserScore());
        user.setUserScore1(oraUser.getUserScore1());
        user.setUserScore2(oraUser.getUserScore2());
        user.setUserScore3(oraUser.getUserScore3());
        user.setUserScore4(oraUser.getUserScore4());
        user.setUserScore5(oraUser.getUserScore5());
        user.setUserScore6(oraUser.getUserScore6());
        user.setUserScore7(oraUser.getUserScore7());
        user.setUserScore8(oraUser.getUserScore8());
        user.setUserScore9(oraUser.getUserScore9());
        user.setLastActive(oraUser.getLastActive());
        return user;
    }


    public static AreasInDk convertArea(AreasInDkOracle oraArea) {
	AreasInDk area = new AreasInDk();
	area.setAreaId("" + oraArea.getAreaId());
	area.setNameOfArea(oraArea.getNameOfArea());
	area.setPolygonCol(oraArea.getPolygonCol());
	return area;
    }
    
    private static UserRole createUserRole(UserOracle oraUser) {
        UserRole userRole = new UserRole();
        userRole.setRoleId(oraUser.getRole().getRoleId());
        userRole.setRoleName(oraUser.getRole().getRoleName());
        return userRole;
    }

    public static UserPermissions convertUserPermission(UserPermissionsOracle oraUserPermission) {
        UserPermissions userPermission = new UserPermissions();
        userPermission.setPermissionId(oraUserPermission.getPermissionId());
        userPermission.setPermissionName(oraUserPermission.getPermissionName());
//        userPermission.set
        return userPermission;
    }

    public static UserRolePermissions convertUserRolePermissions(UserRolePermissionsOracle oraUserRolePermissions) {
        UserRolePermissions userRolePermissions = new UserRolePermissions();
        userRolePermissions.setPermissionId(oraUserRolePermissions.getPermissionId());
        userRolePermissions.setRoleId(oraUserRolePermissions.getRoleId());
        return userRolePermissions;
    }

    public static UserRole convertUserRole(UserRoleOracle oraUserRole) {
        UserRole userRole = new UserRole();
        userRole.setRoleId(oraUserRole.getRoleId());
        userRole.setRoleName(oraUserRole.getRoleName());
        final Set<UserPermissionsOracle> oraUserPermissions = oraUserRole.getPermissions();
        Set<UserPermissions> userPermissions = new TreeSet<UserPermissions>();
/*
        oraUserPermissions.forEach(oraPermission -> {
            UserPermissions permission = new UserPermissions();
            permission.setPermissionId(oraPermission.getPermissionId());
            permission.setPermissionName(oraPermission.getPermissionName());
            userPermissions.add(permission);
        });
*/
        userRole.setPermissions(userPermissions);
        return userRole;
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
        object.setLikes(oraObject.getLikes());

        object.setKeywords((Set<Tag>) oraObject.getKeywords().stream()
                .map(oraTag-> {return convertTag((TagOracle) oraTag);})
                        .collect(Collectors.toSet()));

        object.setCategories((Set<Category>) oraObject.getCategories().stream()
                .map(oraCategory->{return convertCategory((CategoryOracle) oraCategory);})
                .collect(Collectors.toSet()));

        return object;
    }

    private static Geometry convertPoint(JGeometryType point) {
        if (point == null) {
            return null;
        }
        GeometryFactory geoFactory = JTSFactoryFinder.getGeometryFactory();
        double lat = point.getPoint()[0];
        double lon = point.getPoint()[1];
        return geoFactory.createPoint(new Coordinate(lat, lon));
    }

    public static Tag convertTag(TagOracle tagOracle) {
        Tag tag = new Tag();
        tag.setId(tagOracle.getId());
        tag.setTag_value(tagOracle.getTag_value());
        tag.setCreator(tagOracle.getCreator());
        tag.setXlink_to(tagOracle.getXlink_to());
        tag.setTimestamp(tagOracle.getTimestamp());
        return tag;
    }

    public static Comment convertComment(CommentOracle commentOracle) {
        Comment comment = new Comment();
        comment.setText(commentOracle.getText());
        comment.setCreator(commentOracle.getCreator());
        comment.setXlink_to(commentOracle.getXlink_to());
        comment.setId(commentOracle.getId());
        comment.setHost_uri(commentOracle.getHost_uri());
        return comment;
    }

    public static XLink convertXLink(XLinkOracle xlinkOracle) {
        XLink xlink = new XLink();
        xlink.setId(xlinkOracle.getId());
        xlink.setXlink_from(xlinkOracle.getXlink_from());
        xlink.setXlink_to(xlinkOracle.getXlink_to());
        xlink.setXlink_type(xlinkOracle.getXlink_type());
        xlink.setXlink_role(xlinkOracle.getXlink_role());
        xlink.setCreator(xlinkOracle.getCreator());
        xlink.setTimestamp(xlinkOracle.getTimestamp());
        xlink.setXlink_title(xlinkOracle.getXlink_title());
        return xlink;
    }
}

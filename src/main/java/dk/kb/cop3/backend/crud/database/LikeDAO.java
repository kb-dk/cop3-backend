package dk.kb.cop3.backend.crud.database;

/**
 * @author: Andreas B. Westh
 * Date: 12/6/11
 * Time: 11:38 AM
 */
public interface LikeDAO {
    int increaseNumberOfLikesWithOne(String objectUri) throws Exception;

    int getNumberOfLikes(String objectUri) throws Exception;
}

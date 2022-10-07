package dk.kb.cop3.backend.crud.database.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.geometric.PGpoint;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class Point implements UserType, Serializable {

    double lat;
    double lng;

    public Point() {
        this.lat = 0;
        this.lng = 0;
    }

    public Point(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] {Types.OTHER};
    }

    @Override
    public Class returnedClass() {
        return Point.class;
    }

    @Override
    public boolean equals(Object o1, Object o2) throws HibernateException {
        Point p1 = (Point) o1;
        Point p2 = (Point) o2;
        return  p1 == null && p2 == null || (
                p1 != null && p2 != null &&
                p1.getLat() == p2.getLat() &&
                p1.getLng() == p2.getLng());
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return ((Point) o).hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException, SQLException {
        if (resultSet.wasNull()) {
            return null;
        }
        PGpoint pGpoint = (PGpoint) resultSet.getObject(strings[0]);
        if (pGpoint.isNull()) {
            return null;
        }
        return new Point(pGpoint.x, pGpoint.y);
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {
        if (Objects.isNull(o)) {
            preparedStatement.setObject(i,new PGpoint(0,0));

        } else {
            Point p = (Point) o;
            preparedStatement.setObject(i,new PGpoint(p.getLat(),p.getLng()));
        }
    }

    @Override
    public Object deepCopy(Object o) throws HibernateException {
        if( o == null)
            return null;
        if( o instanceof Point) {
            Point p = (Point) o;
            return new Point(p.getLat(), p.getLng());
        } else {
            return null;
        }
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        return (Serializable) deepCopy( o);

    }

    @Override
    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        return deepCopy( serializable);
    }

    @Override
    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        return (JGeometryType) o;
    }

    @Override
    public String toString() {
        return "Point{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}

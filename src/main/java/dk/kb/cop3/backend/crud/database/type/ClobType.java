package dk.kb.cop3.backend.crud.database.type;

import oracle.sql.CLOB;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.BufferedReader;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ClobType implements UserType, Serializable {
    private static final long serialVersionUID = 1L;
    private String stringValue = null;

    private static final int[] SQL_TYPES = {Types.CLOB};

    public ClobType() {
        stringValue = "";
    }

    public ClobType(String s) {
        this.stringValue = s;
    }

    public ClobType(ClobType clobType) {
        this.stringValue = clobType.getStringValue();
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public void setStringValue(String s) {
        this.stringValue = s;
    }

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return String.class;
    }

    public int hashCode(Object o) throws HibernateException {
        return ((ClobType) o).hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException, SQLException {
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {

    }

    @Override
    public boolean equals(Object arg0) {
        return equals(this, arg0);
    }

    public boolean equals(Object obj1, Object obj2) throws HibernateException {

        /* check we are dealing with non-null objects of the correct type */
        if (obj1 instanceof ClobType && obj2 instanceof ClobType && obj1 != null && obj2 != null) {
            return ((ClobType) obj1).getStringValue().equals(((ClobType) obj2).getStringValue());
        } else {
            return false;
        }
    }

    /* calls the load method */
    public Object nullSafeGet(ResultSet resultSet, String[] strings, Object o) throws HibernateException, SQLException {
        try {
            CLOB clob = (CLOB) resultSet.getObject(strings[0]);
            StringBuffer sb = new StringBuffer();
            String s;
            BufferedReader br = new BufferedReader(clob.getCharacterStream());
            while ((s = br.readLine()) != null)
                sb.append(s);
            return sb.toString();
        } catch (java.io.IOException ex) {
            throw new HibernateException("unable to convert clob to string ioexception: " + ex.getMessage(), ex);
        }
    }

    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i) throws HibernateException, SQLException {
        if (o == null) {
            preparedStatement.setNull(i, Types.CLOB);
        } else {
            if (o instanceof ClobType) {
                CLOB clob = CLOB.empty_lob();
                clob.putString(0, (String) o);
                preparedStatement.setObject(i, clob);
            }
        }

    }

    public Object deepCopy(Object o) throws HibernateException {
        if (o == null)
            return null;
        if (o instanceof ClobType) {
            return new ClobType(((ClobType) o).getStringValue());
        } else {
            return null;
        }
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object o) throws HibernateException {
        return (Serializable) deepCopy(o);
    }

    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        return deepCopy(serializable);
    }

    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        return (ClobType) o;
    }


}    

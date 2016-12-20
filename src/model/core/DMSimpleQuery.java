package model.core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by ianagez on 20.12.2016.
 */
public class DMSimpleQuery {
    String QUERY=null;
    Object[] PARAMETERS = new Object[]{};
    ArrayList<HashMap<String,Object>> RESULT=null;

    private DMSimpleQuery(String QUERY) {
        this.QUERY = QUERY;
    }
    public static DMSimpleQuery instance(String sQuery){
        return new DMSimpleQuery(sQuery);
    }
    public DMSimpleQuery select(DBUserSession dbus) throws DataModelException {
        try {
            RESULT = dbus.getDataListFromSQLQuery(QUERY,PARAMETERS);
        } catch (SQLException e) {
            throw new DataModelException("Failed select! Reason:"+e.getLocalizedMessage());
        }
        return this;
    }
    public DMSimpleQuery putResultTo(HashMap outData, String itemName){
        outData.put(itemName, RESULT);
        return this;
    }
    public DMSimpleQuery putResultItemTo(HashMap outData, String itemName) throws DataModelException {
        if (RESULT.size()>1) throw new DataModelException("Failed putResultItemTo! Reason: result contains more one items!");
        if (RESULT.size()==0) outData.put(itemName, null);
        outData.put(itemName, RESULT.get(0));
        return this;
    }
    public DMSimpleQuery putResultItemValueTo(HashMap outData, String itemName) throws DataModelException {
        if (RESULT.size()>1) throw new DataModelException("Failed putResultItemValueTo! Reason: result contains more one items!");
        if (RESULT.size()==0) outData.put(itemName, null);
        outData.put(itemName, RESULT.get(0).values().toArray()[0]);
        return this;
    }

    public DMSimpleQuery setParameter(String sParamValue) {
        Object[] oldPARAMETERS = PARAMETERS;
        PARAMETERS = new Object[PARAMETERS.length+1];
        System.arraycopy(oldPARAMETERS, 0, PARAMETERS, 0, oldPARAMETERS.length );
        PARAMETERS[PARAMETERS.length-1] = sParamValue;
        return this;
    }
}

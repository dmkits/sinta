package model.core;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ianagez on 20.12.2016.
 */
public class DMSimpleQuery {
    String QUERY = null;
    Object[] PARAMETERS = new Object[]{};
    ArrayList<HashMap<String, Object>> RESULT = null;
//    public static final Charset UTF = Charset.forName("UTF-8");

    private DMSimpleQuery(String QUERY) {
        this.QUERY = QUERY;
    }

    public static DMSimpleQuery instance() {
        return new DMSimpleQuery(null);
    }
    public static DMSimpleQuery instance(String sQuery) {
        return new DMSimpleQuery(sQuery);
    }
    public DMSimpleQuery select(DBUserSession dbus) throws DataModelException {
        try {
            RESULT = dbus.getDataListFromSQLQuery(QUERY, PARAMETERS);
        } catch (SQLException e) {
            throw new DataModelException("Failed select! Reason:" + e.getLocalizedMessage());
        }
        return this;
    }
    public DMSimpleQuery putResultTo(HashMap outData, String itemName) {
        outData.put(itemName, RESULT);
        return this;
    }

    public HashMap<String, Object> getResultItem() throws DataModelException {
        if (RESULT.size() > 1)
            throw new DataModelException("Failed getResultItem! Reason: result contains more one items!");
        if (RESULT.size() == 0) return null;
        return RESULT.get(0);
    }

    public DMSimpleQuery putResultItemTo(HashMap outData, String itemName) throws DataModelException {
        outData.put(itemName, getResultItem());
        return this;
    }

    public DMSimpleQuery addResultItemToList(HashMap outData, String itemName) throws DataModelException {
        HashMap<String, Object> resultItem = getResultItem();
        ArrayList<HashMap> outDataList = (ArrayList<HashMap>) outData.get(itemName);
        if (outDataList == null) {
            outDataList = new ArrayList<HashMap>();
            outData.put(itemName, outDataList);
        }
        outDataList.add(resultItem);
        return this;
    }
    public DMSimpleQuery addResultToList(HashMap outData, String itemName) throws DataModelException {
        for (int i = 0; i < RESULT.size(); i++) {
            HashMap<String, Object> resultItem = RESULT.get(i);
            ArrayList<HashMap> outDataList = (ArrayList<HashMap>) outData.get(itemName);
            if (outDataList == null) {
                outDataList = new ArrayList<HashMap>();
                outData.put(itemName, outDataList);
            }
            outDataList.add(resultItem);
        }
        return this;
    }

    public DMSimpleQuery putResultItemValueTo(HashMap outData, String itemName) throws DataModelException {
        HashMap<String, Object> resultItem = getResultItem();
        if (resultItem != null) outData.put(itemName, resultItem.values().toArray()[0]);
        return this;
    }

    public DMSimpleQuery setParameter(String sParamValue) {
        Object[] oldPARAMETERS = PARAMETERS;
        PARAMETERS = new Object[PARAMETERS.length + 1];
        System.arraycopy(oldPARAMETERS, 0, PARAMETERS, 0, oldPARAMETERS.length);
        PARAMETERS[PARAMETERS.length - 1] = sParamValue;
        return this;
    }

    public DMSimpleQuery addToResultItem(String label, String value) {
        if (RESULT.size() == 0) return this;
        HashMap<String, Object> firstResultItem = RESULT.get(0);
        firstResultItem.put(label, value);
        return this;
    }

    public DMSimpleQuery addToAllResultItems(String label, String value) {
        if (RESULT.size() == 0) return this;
        for (int i = 0; i < RESULT.size(); i++) {
            HashMap<String, Object> resultItem = RESULT.get(i);
            resultItem.put(label, value);
        }
        return this;
    }

    public DMSimpleQuery replaceResultItemName(String existsItemName, String newItemName) throws DataModelException {
        for (int i = 0; i < RESULT.size(); i++) {
            HashMap<String, Object> resultItem = RESULT.get(i);
            if (resultItem.containsKey(existsItemName)) {
                resultItem.put(newItemName, resultItem.get(existsItemName));
                resultItem.remove(existsItemName);
            }
        }
        return this;
    }
    public DMSimpleQuery load(String path) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            QUERY = sb.toString();
        } finally {
            br.close();
        }
        return this;
    }
}

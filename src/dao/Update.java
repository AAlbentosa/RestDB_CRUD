package dao;

import org.json.simple.JSONObject;

public class Update {
	
	public static String putAll(final DBConnection client, String collection,String query) {
        return client.put(collection,query);
    }
	
    public static String putRequest(DBConnection client,String collection, JSONObject parameters) {
    	String resultPut = client.put(collection, parameters.toJSONString());
    	return resultPut;
    }

}

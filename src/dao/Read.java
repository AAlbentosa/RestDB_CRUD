package dao;

import org.json.simple.JSONObject;

public class Read {

	public static String getAll(final DBConnection client, String collection) {
        return client.get(collection);
    }

	public static String getByValue(DBConnection client, String collection, JSONObject parameters) {
		String result = client.get(collection+"?q="+parameters.toString());
		return result;
	}
}

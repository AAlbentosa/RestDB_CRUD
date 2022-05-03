package dao;

import org.json.simple.JSONObject;

public class Delete {
	
	public static void deleteAll(final DBConnection client, String collection) {
		String result = client.delete(collection);
		 System.out.println("result postRequest : " + result);
	}

}

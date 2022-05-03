package dao;

import org.json.simple.JSONObject;

public class Create {
	
	public static void sendCreateRequest(final DBConnection client, JSONObject jsonobject, String Collection) {
        String queryResult = client.post(Collection, jsonobject.toJSONString());
        System.out.println("result postRequest : " + queryResult);
    }
}

package dao;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DBConnection {
	private final String TAG = DBConnection.class.getSimpleName();
    private final String BASE_URI = "https://erasmus-b4d7.restdb.io/rest/"; // URL
    private final String API_KEY = "b392855fa509c0fcff6bd4efbc2a5493cf62e"; // API KEY
    private final String CONTENT_TYPE = "application/json";
    private final int TIMEOUT = 2000;// 2000ms = 2seconds

    //HTTP protocol
    private final String POST = "POST";
    private final String DELETE = "DELETE";
    private final String GET = "GET";
    private final String PUT = "PUT";

    private  HttpURLConnection createConnection(final String requestMethod, final String url) throws IOException {
        HttpURLConnection connection = null;
        URL finalUrl = new URL(BASE_URI + url);
        connection = (HttpURLConnection)finalUrl.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("x-apikey", API_KEY);
        connection.setRequestProperty("Content-Type", CONTENT_TYPE);
        connection.setConnectTimeout(TIMEOUT);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        return connection;
    }

    private void sentPostRequest(final HttpURLConnection connection, final String parameters) throws IOException {
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();
    }

    private String executeHTTPRequest(final String requestMethod, final String url) {
        return executeHTTPRequest(requestMethod,url, null);
    }

    private  String executeHTTPRequest(final String requestMethod, final String url, final String parameters)  {
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder(); 
        try {
            connection = createConnection(requestMethod, url);

            if (POST.equals(requestMethod) || PUT.equals(requestMethod))
                sentPostRequest(connection, parameters);

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line); 
            }
            rd.close();
        } catch (IOException e) {
        	e.printStackTrace();
            System.err.println("Cant connect to the server. Please try later on");
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }

    public  String post(final String collection, final String objectToAdd) {
        return executeHTTPRequest(POST, collection, objectToAdd);
    }

    
    public  String get(final String collection) {
        return executeHTTPRequest(GET, collection);
    }

    public  String put(final String collection, final String query) {
        return executeHTTPRequest(PUT, collection, query);
    }

    public  String delete(final String collection) {
        return executeHTTPRequest(DELETE, collection);
    }
}
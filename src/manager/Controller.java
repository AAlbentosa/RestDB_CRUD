package manager;

import java.text.ParseException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import dao.DBConnection;
import dao.Delete;
import dao.Read;
import dao.Update;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dao.Create;

public class Controller {
	private DBConnection connection;
	private Scanner sc;
	private static final String COLLECTION_KEBABS = "kebabs";
	private static final String COLLECTION_RESTAURANTES = "restaurants";
	private static final String COLLECTION_CLUBS = "night-clubs";
	private static final String COLLECTION_COSITAS = "faxx";
	private HashMap<String, List<String>> collectionsFields;

	public Controller() {
		sc = new Scanner(System.in);
		connection = new DBConnection();
		collectionsFields = new HashMap();
		collectionsFields.put(COLLECTION_KEBABS,
				List.of("Name (String)", "Durum_price (Price)", "Donner_price (Price)", "Chips_included (bool)"));
		collectionsFields.put(COLLECTION_RESTAURANTES, List.of("name (String)", "best_dish (String)", "price (Price)"));
		collectionsFields.put(COLLECTION_CLUBS, List.of("name (String)", "entry_price (Price)", "beer_price (Price)"));
		collectionsFields.put(COLLECTION_COSITAS, List.of("name (String)", "description (String)", "img (String)"));
	}

	public void init() {
		showCollectionsMenu();
	}

	private void showCollectionsMenu() {
		boolean exit = false;

		while (!exit) {
			System.out.println(
					"Bienvenido al CRUD Erasmus-RestDB, aqui podras consultar o modificar todo tipo de datos relacionados con nuestro Erasmus.");
			System.out.println("Selecciona a que coleccion quieres acceder:");
			System.out.println("1.-kebabs (Descubre los mejores kebabs de Dublin)");
			System.out.println("2.-Restaurantes (Descubre los mejores sitios para comer en Dublin)");
			System.out.println("3.-Clubes nocturnos (La mejor mosica y el mejor ambiente de Dublin)");
			System.out.println("4.-Easter eggs (Items recopilados durante nuestras aventuras nocturnas en Dublin)");
			System.out.println("0.-Salir");
			switch (sc.nextInt()) {
			case 1:
				showCrudMenu(COLLECTION_KEBABS);
				break;
			case 2:
				showCrudMenu(COLLECTION_RESTAURANTES);
				break;
			case 3:
				showCrudMenu(COLLECTION_CLUBS);
				break;
			case 4:
				showCrudMenu(COLLECTION_COSITAS);
				break;
			case 0:
				exit = true;
				break;
			}
		}

	}

	private void showCrudMenu(String collection) {
		boolean exit = false;

		while (!exit) {
			System.out.println("Coleccion: " + collection
					+ ", selecciona una opcion:\n1.-Create\n2.-Read\n3.-Update\n4.-Delete\n0.-Atras");
			switch (sc.nextInt()) {
			case 1:
				createMenu(collection);
				break;
			case 2:
				selectMenu(collection);
				break;
			case 3:
				updateMenu(collection);
				break;
			case 4:
				delete(collection);
				break;
			case 0:
				exit = true;
				break;
			}
		}
	}
	
	private void updateMenu(String collection) {
		boolean exit = false;
		while (!exit) {
			System.out.println("Quieres actualizar todo o un campo concreto?");
			System.out.println("1.-Actualizar todo\n2.-Actualizar campo concreto\n0.-Atras");
			switch (sc.nextInt()) {
			case 1:
				updateAll(collection);
				break;
			case 2:
				putRequest(collection);
				break;
			case 0:
				exit = true;
				break;
			}
		}
	}
	
	/**
	 * Actualiza todos los valores de una row de la database (no esta acabado)
	 * @param collection
	 */
	private void updateAll (String collection) {
		for (int j = 0; j < collectionsFields.get(collection).size(); j++) {
			System.out.println(j + "- " + collectionsFields.get(collection).get(j));
		}
		String query = "";
		switch (sc.nextInt()) {
		case 1:	
			for (int i = 0; i < collectionsFields.get(collection).size(); i++) {
				System.out.println("Introduce el campo: " + collectionsFields.get(collection).get(i));
				query = query + sc.nextLine() + ",";
			}
		}
		
		String info = Update.putAll(connection, collectionsFields.get(collection).get(1),query);
		toPrettyFormat(info);
	}

	private void delete(String collection) {
		boolean exit = false;
		while (!exit) {
			System.out.println("Vas a borrar todo, estas seguro?");
			System.out.println("1- Si \n2- No");
			if (sc.nextInt() == 1) {
				Delete.deleteAll(connection, collection);
			} else
				exit = true;
		}

	}

    private String putRequest(String collection) {
        JSONParser parser = new JSONParser();
        Object obj = null;
        String id = null;
        
        String info = Read.getAll(connection, collection);
        
        try {
            obj = parser.parse(info);
            JSONArray results = (JSONArray)(obj);
            if (!results.isEmpty() && results.size() > 0)
            {

                JSONObject firstOne = (JSONObject)results.get(0); // take first item
                System.out.println(firstOne.toJSONString());
                
                JSONObject firstItem = new JSONObject(); // take first item
                firstItem.put("name", "Temple Bar");
                
                id = (String)firstOne.get("_id");
                Update.putRequest(connection, collection+ "/" + id, firstItem);
            }
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return id;
    }

	private void selectMenu(String collection) {
		boolean exit = false;
		while (!exit) {
			System.out.println("Quieres seleccionar todo o un campo concreto?");
			System.out.println("1.-Seleccionar todo\n2.-Seleccionar campo concreto\n0.-Atras");
			switch (sc.nextInt()) {
			case 1:
				String info = Read.getAll(connection, collection);
				toPrettyFormat(info);
				break;
			case 2:
				selectByField(collection);
				break;
			case 0:
				exit = true;
				break;
			}
		}
	}

	public static void toPrettyFormat(String jsonString) {
		JsonParser parser = new JsonParser();
		JsonArray json = parser.parse(jsonString).getAsJsonArray();
		
		System.out.println("Resultados :");
		if(!json.isEmpty())
			for (JsonElement je: json) 
				System.out.println(je.toString().replaceAll("\"", ""));
		else
			System.out.println("No se encontraron resultados.");
		System.out.println();
	}

	private void selectByField(String collection) {
		int opcion;
		JSONObject object = new JSONObject();

		do {
			System.out.println("Porfavor seleccione campo:");
			for (int i = 0; i < collectionsFields.get(collection).size(); i++)
				System.out.println((i + 1) + ".-" + collectionsFields.get(collection).get(i));

			opcion = sc.nextInt();
		} while (opcion < 0 || opcion > collectionsFields.get(collection).size());

		String field = collectionsFields.get(collection).get(opcion - 1);
		System.out.println("Introduce el "+field.split(" ")[0]+" porfavor");
		object.put(field.split(" ")[0], getParametericedInput(field.split(" ")[1],field.split(" ")[0]));
		toPrettyFormat(Read.getByValue(connection, collection, object));
	}
	
	private void createMenu(String collection) {
		System.out.println("Desde aqui puedes introducir nuevas entradas en: " + collection);
		System.out.println("Por favor, introduce:");
		JSONObject object = new JSONObject();
		String[] fieldData;

		for (String key : collectionsFields.get(collection)) {
			fieldData = key.split(" ");
			System.out.println(key + ":");
			object.put(fieldData[0], getParametericedInput(fieldData[1],fieldData[0]));
		}
		Create.sendCreateRequest(connection, object, collection);
	}

	private String getParametericedInput(String type, String name) {
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			try {
				switch (type) {
				case "(String)":
					return sc.nextLine();
				case "(bool)":
					return selectBoolean();
				case "(Price)":
					double input=Double.parseDouble(sc.nextLine());
					return String.valueOf(input);
				}
			} catch (InputMismatchException e) {
				System.out.println("Por favor, introduce un dato del formato solicitado");
			}
		}
	}

	private String selectBoolean() {
		System.out.println("Introduce y(yes) o n(no)");
		sc.nextLine();
		if(sc.nextLine().equals("y"))
			return "true";
		else
			return "false";
	}
}

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // CSV - JSON PARSER

        //создаем паттерн
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        //имя файла CSV, из которого будут считываться данные
        String fileName = "data.csv";
        //достаем данные из CSV файла и парсим в объект типа List
        List<Employee> list = parseCSV(columnMapping, fileName);
        // данные из списка сохраняем в String
        String json = listToJson(list);
        //записываем в файл типа json
        writeString(json, "newdata.json");

        //  XML - JSON парсер

        //создаем список сотрудников из файла XML
        List<Employee> xmlList = parseXML("data.xml");
        // преобразуем полученный список в String
        String xmlString = listToJson(xmlList);
        //сохраняем в файл Json
        writeString(xmlString, "data2.json");

        // преобразование объектов JSON в классы Java
        String readFromJson = readToString("newdata.json");
        List<Employee> listOfEmployeesFromJson = jsonToList(readFromJson);

        //выводим в консоль элементы полученного списка
        for (Employee employee : listOfEmployeesFromJson) {
            System.out.println(employee);
        }


    }

    //функция для считывания данных из CSV файла и сохранения их в объект типа List
    public static List<Employee> parseCSV(String[] columnMapping, String filename) {
        List<Employee> employees = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(filename))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            employees = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employees;
    }

    // функция для сохранения в String в формате json
    public static String listToJson(List<Employee> employees) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(employees, listType);
    }

    // функция для записи String json в файл типа json
    public static void writeString(String json, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // функция для получения списка сотрудников из XML документа
    public static List<Employee> parseXML(String file) {
        List<Employee> employees = new ArrayList<Employee>();
        Employee employee = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(file));

            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for(int i = 0; i < nodeList.getLength(); i++) {
                Node node1 = nodeList.item(i);
                if (Node.ELEMENT_NODE == node1.getNodeType()) {
                    Element element = (Element) node1;

                    employee = new Employee();

                    employee.setId(Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()));
                    employee.setFirstName(element.getElementsByTagName("firstName").item(0).getTextContent());
                    employee.setLastName(element.getElementsByTagName("lastName").item(0).getTextContent());
                    employee.setCountry(element.getElementsByTagName("country").item(0).getTextContent());
                    employee.setAge(Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent()));

                    employees.add(employee);
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        return employees;
    }

    //содержимое из файла JSON кладем в String
    public static String readToString(String filepath){
        StringBuilder textFromJson = new StringBuilder("");

        try (BufferedReader buffer = new BufferedReader(new FileReader(filepath))){
            String line = null;

            while ((line = buffer.readLine()) != null) {
                textFromJson.append(line);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return textFromJson.toString();
    }

    // преобразуем JSON-строку в классы Java
    public static List<Employee> jsonToList(String json) {

        // получаем из строчки json массив JSONArray
        JSONParser parser = new JSONParser();
        JSONArray jsonArray;
        try {
            jsonArray = (JSONArray) parser.parse(json);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        //создаем экземпляр Gson
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        //создаем список для объектов Employee
        List<Employee> employees = new ArrayList<>();

        // из массива jsonArray получаем экземпляры класса Employee и кладем их в список
        for (Object emp : jsonArray) {
            Employee employee = gson.fromJson(emp.toString(), Employee.class);
            employees.add(employee);
        }

        return employees;
    }
}

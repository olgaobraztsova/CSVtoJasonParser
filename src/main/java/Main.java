import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {
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
}

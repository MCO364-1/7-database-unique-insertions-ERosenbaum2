import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String connectionUrl = "jdbc:sqlserver://database-1.ckxf3a0k0vuw.us-east-1.rds.amazonaws.com;"
                + "database=XXXXXXXXXX;"//deleted for safety
                + "user=XXXXXXXXXX;"//deleted for safety
                + "password=XXXXXXXXXX;"//deleted for safety
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=30;";

        ResultSet resultSet;
        Scanner input = new Scanner(System.in);
        System.out.println("Enter you first name: ");
        String firstName = input.nextLine().toUpperCase();
        System.out.println("Enter you last name: ");
        String lastName = input.nextLine().toUpperCase();
        String insertSql = "INSERT INTO PEOPLE (FirstName, LastName) VALUES (?,?);";
        String selectSql = "SELECT FirstName FROM People";
        Set people = new HashSet();
        Map<Character, Integer> map = new HashMap<>();

        try(Connection connection = DriverManager.getConnection(connectionUrl)) {
            try(Statement statement = connection.createStatement()) {
                resultSet = statement.executeQuery(selectSql);
                //put all the names from the database in a set
                while (resultSet.next()) {
                    people.add(resultSet.getString(1));
                }
            }

            //if the name is not in the database, insert it into the database
            if(people.add(firstName)){
                try(PreparedStatement prepsInsertProduct = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);) {
                    prepsInsertProduct.setString(1, firstName);
                    prepsInsertProduct.setString(2, lastName);
                    prepsInsertProduct.execute();
                }
            } else {
                System.out.println("That name is already in the database!");
            }
            //put all the names in the database in a map and count the number of times each letter appears
            for (Object o: people) {
                char firstLetter = o.toString().charAt(0);
                if (map.containsKey(firstLetter)) {
                    map.put(firstLetter, map.get(firstLetter) + 1);
                } else {
                    map.put(firstLetter, 1);
                }
            }
            //print out the map and the number of times each letter appears
            System.out.println("There are " + people.size() + " people in the database");
            for(Map.Entry<Character, Integer> entry: map.entrySet()){
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        //Membuat string koneksi ke postgres
        String username = "username";
        String password = "password";
        String database = "etl_csv_postgres";
        String connectionString = "jdbc:postgresql://127.0.0.1:5432/"+database+"?user="+username+"&password="+password;

        try {
            //Melakukan koneksi dan insisialisasi statement manipulasi query ke postgres
            Connection conn = DriverManager.getConnection(connectionString);
            Statement statement = conn.createStatement();

            //Membuat table 'tingkat_pendidikan' dan ignore jika sudah ada
            String createTable = "CREATE TABLE IF NOT EXISTS tingkat_pendidikan (" +
                    "id INTEGER NOT NULL PRIMARY KEY, " +
                    "province_code INTEGER, " +
                    "province_name VARCHAR(255), " +
                    "education_level VARCHAR(255), " +
                    "gender VARCHAR(255), " +
                    "total INTEGER)";
            statement.executeUpdate(createTable);

            //Membaca isi file 'ddk_tingkat_pendidikan.csv'
            FileReader file = new FileReader("D:/Java & Python Programming/Java Advanced 4/ddk_tingkat_pendidikan.csv");
            BufferedReader reader = new BufferedReader(file);
            String lineText = null;

            //Ignore first row data dari file csv
            reader.readLine();
            int id = 0;

            while ((lineText = reader.readLine()) != null) {
                //Split data berdasarkan koma dan simpan ke String array
                String[] data = lineText.split(",");

                //Assign data ke variabel berdasarkan spesifik index
                int province_code = Integer.parseInt(data[0]);
                String province_name = data[1].replace("\"", "");
                String education_level = data[2].replace("\"", "");
                String gender = data[3].replace("\"", "");
                int total = Integer.parseInt(data[4]);
                id++;

                //Melakukan insert query per row ke table 'tingkat_pendidikan' dari csv dan ignore jika memiliki id sama
                String insertTable = "INSERT INTO tingkat_pendidikan VALUES " +
                        "("+id+", "+province_code+", '"+province_name+"', '"+education_level+"', '"+gender+"', "+total+") " +
                        "ON CONFLICT (id) DO NOTHING";
                statement.executeUpdate(insertTable);
            }

            //Menutup koneksi pembacaan file 'ddk_tingkat_pendidikan.csv'
            reader.close();
            file.close();

            //Membaca seluruh row dan column dari table 'tingkat_pendidikan'
            String selectQuery = "SELECT * FROM tingkat_pendidikan";
            ResultSet result = statement.executeQuery(selectQuery);

            //Membaca data query per row
            while (result.next()) {
                System.out.print("ID: " + result.getInt("id") + ", ");
                System.out.print("Province Code: " + result.getInt("province_code") + ", ");
                System.out.print("Province Name: " + result.getString("province_name") + ", ");
                System.out.print("Education: " + result.getString("education_level") + ", ");
                System.out.print("Gender: " + result.getString("gender") + ", ");
                System.out.print("Total: " + result.getInt("total"));
                System.out.println();
            }

            //Menutup koneksi ke database postgres
            statement.close();
            conn.close();

        } catch(SQLException | IOException exc) {
            exc.printStackTrace();
        }
    }
}
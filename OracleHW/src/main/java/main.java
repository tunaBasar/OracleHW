import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class main {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:ORCLCDB";
    private static final String USER = "SYSTEM";
    private static final String PASSWORD = "oracle";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE numbers (id NUMBER)");
            }


            long startTime = System.nanoTime();
            insertRandomIds(connection, 20000);
            long endTime = System.nanoTime();
            long duration = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            System.out.println("Time to insert 20,000 IDs: " + duration + " ms");


            startTime = System.nanoTime();
            selectRandomIds(connection, 20000);
            endTime = System.nanoTime();
            duration = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            System.out.println("Time to select 20,000 IDs: " + duration + " ms");


            startTime = System.nanoTime();
            insertRandomIds(connection, 100000);
            endTime = System.nanoTime();
            duration = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            System.out.println("Time to insert 100,000 IDs: " + duration + " ms");


            startTime = System.nanoTime();
            selectRandomIds(connection, 100000);
            endTime = System.nanoTime();
            duration = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            System.out.println("Time to select 100,000 IDs: " + duration + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertRandomIds(Connection connection, int count) throws Exception {
        String sql = "INSERT INTO numbers (id) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            Random random = new Random();
            for (int i = 1; i <= count; i++) {
                pstmt.setInt(1, random.nextInt(100000)); // Rastgele ID değeri
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private static void selectRandomIds(Connection connection, int count) throws Exception {
        String sql = "SELECT id FROM numbers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            Random random = new Random();
            for (int i = 1; i <= count; i++) {
                pstmt.setInt(1, random.nextInt(100000));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        rs.getInt("id");
                    }
                }
            }
        }
    }
}

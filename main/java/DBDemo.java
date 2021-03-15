import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class DBDemo {
    public static void main(String[] args) {
        String jdbcULR = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String password = "Deep@123";
        Connection connection;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");
        }catch (ClassNotFoundException e){
            throw new IllegalStateException("can not found the driver in the classpath ..!!",e);
        }
        listDrivers();
        try {
            System.out.println("Connect to: "+jdbcULR);
            connection=DriverManager.getConnection(jdbcULR,userName,password);
            System.out.println("connection successful to:  "+connection);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void listDrivers() {
        Enumeration<Driver> driverList = DriverManager.getDrivers();
        while (driverList.hasMoreElements()){
            Driver driverClass = (Driver) driverList.nextElement();
            System.out.println("-> "+driverClass.getClass().getName());
        }
    }
}

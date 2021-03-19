import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
    private PreparedStatement employeePayRollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    private EmployeePayrollDBService(){}

    public static EmployeePayrollDBService getInstance(){
        if (employeePayrollDBService==null)
            employeePayrollDBService=new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection() throws SQLException {
        String jdbcULR = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String password = "Deep@123";
        Connection connection;
        System.out.println("Connecting To DB: " + jdbcULR);
        connection = DriverManager.getConnection(jdbcULR,userName,password);
        System.out.println("Connection is successful..! " + connection);
        return connection;
    }

    public List<EmployeePayrollData> readData() {
        String sql = "select * from employee_payroll;";
        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        try (Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollDataList = this.getEmployeePayRollData(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollDataList;
    }

    public List<EmployeePayrollData> getEmployeePayRollData(String name) {
        List<EmployeePayrollData>employeePayrollDataList = null;
        if (this.employeePayRollDataStatement==null)
            this.preparedStatementForEmployeeData();
        try {
            employeePayRollDataStatement.setString(1,name);
            ResultSet resultSet = employeePayRollDataStatement.executeQuery();
            employeePayrollDataList = this.getEmployeePayRollData(resultSet);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollDataList;
    }

    private List<EmployeePayrollData> getEmployeePayRollData(ResultSet resultSet) {
        List<EmployeePayrollData>employeePayrollDataList = new ArrayList<>();
        try {
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("salary");
                LocalDate date = resultSet.getDate("start").toLocalDate();
                employeePayrollDataList.add(new EmployeePayrollData(name,id,salary,gender,date));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollDataList;
    }

    private void preparedStatementForEmployeeData() {
        try {
            Connection connection = this.getConnection();
            String sql = "select * from employee_payroll where name= ?;";
            employeePayRollDataStatement = connection.prepareStatement(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int updateEmployeeData(String name, double salary) {
          return this.updateEmployeeDataUsingStatement(name,salary);
    }

    private int updateEmployeeDataUsingStatement(String name, double salary) {
        String sql = String.format("update employee_payroll set salary = %.2f where name = '%s';",salary,name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public List<EmployeePayrollData> readFilteredData(LocalDate date,LocalDate endDate) {
        String sql = String.format("select * from employee_payroll where start between '%s' and '%s';",Date.valueOf(date),Date.valueOf(endDate));
        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        try (Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollDataList = this.getEmployeePayRollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollDataList;
    }

    public double sumOfSalary(String gender) {
        String sql = String.format("select sum(salary) from employee_payroll where gender='%s'", gender);
        double result = 0;
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                result = resultSet.getDouble("sum(salary)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public double avgOfSalary(String gender) {
        String sql = String.format("select avg(salary) from employee_payroll where gender='%s'", gender);
        double result = 0;
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                result = resultSet.getDouble("avg(salary)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int countByGender(String gender) {
        String sql = String.format("select count(gender) from employee_payroll where gender='%s'", gender);
        int result = 0;
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                result = resultSet.getInt("count(gender)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public EmployeePayrollData addEmployeeDataUC7(String name, String gender, double salary, LocalDate date) {
        int employeeID = 0;
        String sql = String.format("insert into employee_payroll (name,gender,salary,start) values ('%s','%s','%s','%s')", name, gender, salary, Date.valueOf(date));
        EmployeePayrollData employeePayrollData = null;
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            int rowAffected = statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    employeeID = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(name, employeeID, salary, gender, date);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollData;
    }

    public EmployeePayrollData addEmployeeData(String name, String gender, double salary, LocalDate date) {
        int employeeID = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try {
            connection = this.getConnection();
            connection.setAutoCommit(false);
        }catch (SQLException e){
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement()) {
            String sql = String.format("insert into employee_payroll (name,gender,salary,start) values ('%s','%s','%s','%s')", name, gender, salary, Date.valueOf(date));
            int rowAffected = statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    employeeID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        try (Statement statement = connection.createStatement()) {
            double deductions = salary * 0.2;
            double taxablePay = salary-deductions;
            double tax = taxablePay * 0.1;
            double netPay = salary-tax;
            String sql = String.format("insert into payroll_details (emp_id, basic_pay, deductions, taxable_pay, tax, net_pay ) values " +
                                       "( %s,%s,%s,%s,%s,%s)", employeeID, salary, deductions, taxablePay, tax, netPay);
            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected==1){
                employeePayrollData = new EmployeePayrollData(name, employeeID, salary, gender, date);
            }
        }catch (SQLException e){
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        try {
            connection.commit();
        }catch (SQLException exception) {
            exception.printStackTrace();
        }finally {
            if (connection != null){
                try {
                    connection.close();
                }catch (SQLException r){
                    r.printStackTrace();
                }
            }
        }
        return employeePayrollData;
    }
}

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class EmployeePayrollServiceTest {
    @Test
    void given3EmpWhenWrittenToFilesShouldMatchEmpEntries() {
        EmployeePayrollData[] arrayOfEmp = {
                new EmployeePayrollData("Deep", 1, 1235),
                new EmployeePayrollData("Bill", 2, 1235),
                new EmployeePayrollData("Mark", 3, 1235),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmp));
        employeePayrollService.empWriteData(EmployeePayrollService.IOService.FILE_IO);
        employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
        long result = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
        System.out.println(result);
        Assertions.assertEquals(3, result);
    }

    @Test
    void given3EmpWhenReadToFilesShouldMatchEmpEntries() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        long result = employeePayrollService.readData(EmployeePayrollService.IOService.FILE_IO);
        Assertions.assertEquals(3, result);
    }

    @Test
    void givenEmployeePayRollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmpPayRollData(EmployeePayrollService.IOService.DB_IO);
        System.out.println(employeePayrollData);
        Assertions.assertEquals(3, employeePayrollData.size());
    }

    @Test
    void givenNewSalaryForEmployee_whenUpdate_shouldSyncWithDB() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmpPayRollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.updateEmployeeSalary("TERISA", 30000000.00, EmployeePayrollService.IOService.DB_IO);
        boolean result = employeePayrollService.checkEmployeePayRollSyncWithDB("TERISA");
        Assertions.assertEquals(true, result);
    }

    @Test
    void givenDateRangeToEmployeePayRollInDB_WhenRetrieved_ShouldMatchFilteredEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        LocalDate date = LocalDate.of(2018,01,01);
        LocalDate endDate= LocalDate.now();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readFilteredEmpPayRollData(EmployeePayrollService.IOService.DB_IO,date,endDate);
        Assertions.assertEquals(3,employeePayrollData.size());
    }

    @Test
    void givenGenderToEmployeeRollDB_whenRetrievedSumOfSalary_shouldMatchExpectedSumOfSalary () {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        String gender = "M";
        double sumOfSalary = employeePayrollService.sumOfSalary(EmployeePayrollService.IOService.DB_IO,gender);
        double expectedSumOfSalary = 60000000.00;
        Assertions.assertEquals(expectedSumOfSalary,sumOfSalary);
    }

    @Test
    void givenGenderToEmployeeRollDB_whenRetrievedAverage_shouldMatchExpectedAverageOfSalary () {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        String gender = "M";
        double sumOfSalary = employeePayrollService.avgOfSalary(EmployeePayrollService.IOService.DB_IO,gender);
        double expectedSumOfSalary = 35000000.00;
        Assertions.assertEquals(expectedSumOfSalary,sumOfSalary);
    }

    @Test
    void givenGenderToEmployeeRollDB_whenRetrievedCount_shouldMatchExpectedCount () {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        String gender = "M";
        int sumOfSalary = employeePayrollService.countByGender(EmployeePayrollService.IOService.DB_IO,gender);
        int expectedSumOfSalary = 2;
        Assertions.assertEquals(expectedSumOfSalary,sumOfSalary);
    }

    @Test
    void givenNewEmployeeToEmployeeRollDB_whenAdded_shouldSyncWithDB () {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmpPayRollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.addEmployee("MARK","M",50000000.00,LocalDate.now());
        boolean result = employeePayrollService.checkEmployeePayRollSyncWithDB("MARK");
        Assertions.assertTrue(result);
    }

    @Test
    void givenNewEmployeeToEmployeeRollDB_whenAdded_shouldMatchWithEntries () {
        EmployeePayrollData[] payrollData = {
                new EmployeePayrollData("Jeff", 0, 1000000.00, "F", LocalDate.now()),
                new EmployeePayrollData("Bill", 0, 2000000.00, "M", LocalDate.now()),
                new EmployeePayrollData("Sunder", 0, 4000000.00, "M", LocalDate.now()),
                new EmployeePayrollData("Mukesh", 0, 44000000.00, "M", LocalDate.now()),
                new EmployeePayrollData("Anil", 0, 5000000.00, "M", LocalDate.now()),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmpPayRollData(EmployeePayrollService.IOService.DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeeToDB(Arrays.asList(payrollData));
        Instant end = Instant.now();
        System.out.println("Duration withOut thread  "+Duration.between(start,end));
        Instant threadStart = Instant.now();
        employeePayrollService.addEmployeeToDBWithThreads(Arrays.asList(payrollData));
        Instant threadEnd = Instant.now();
        System.out.println("Duration with thread  "+Duration.between(threadStart,threadEnd));
        employeePayrollService.printData(EmployeePayrollService.IOService.DB_IO);
        Assertions.assertEquals(11,employeePayrollService.countEntries(EmployeePayrollService.IOService.DB_IO));
    }

    public void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }

    @Test
    void givenEmployeeDataInJSONServer_WhenRetrieved_shouldMatchYheCount() {
        EmployeePayrollData[] payrollData = getEmployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(payrollData));
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        Assertions.assertEquals(2,entries);
    }

    public EmployeePayrollData[] getEmployeeList() {
        setup();
        Response response = RestAssured.get("/employee_payroll");
        System.out.println("Employee Payroll Entries In JsonServer:\n"+ response.asString());
        EmployeePayrollData[] arrayOfEmp = new Gson().fromJson(response.asString(),EmployeePayrollData[].class);
        return arrayOfEmp ;
    }

    @Test
    void givenNewEmployee_whenAdded_shouldMatchStatusCode() {
        EmployeePayrollService employeePayrollService;
        EmployeePayrollData[] payrollData = getEmployeeList();
        employeePayrollService = new EmployeePayrollService(Arrays.asList(payrollData));

        EmployeePayrollData employeePayrollData =null;
        employeePayrollData = new EmployeePayrollData("Mark",0,300000);
        Response response = addEmployeeToJSONServer(employeePayrollData);
        int statusCode = response.getStatusCode();
        Assertions.assertEquals(201,statusCode);

        employeePayrollData = new Gson().fromJson(response.asString(),EmployeePayrollData.class);
        employeePayrollService.addEmployeeToJSON(employeePayrollData);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        Assertions.assertEquals(3,entries);
    }

    private Response addEmployeeToJSONServer(EmployeePayrollData employeePayrollData) {
        String empJSON = new  Gson().toJson(employeePayrollData);
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Content-Type","application/json");
        requestSpecification.body(empJSON);
        return requestSpecification.post("/employee_payroll");
    }

    @Test
    void givenListOfNewEmployee_whenAdded_shouldMatch201Response() {
        EmployeePayrollService employeePayrollService;
        EmployeePayrollData[] payrollData = getEmployeeList();
        employeePayrollService = new EmployeePayrollService(Arrays.asList(payrollData));

        EmployeePayrollData[] data = {
                new EmployeePayrollData("Sunder",1,60000.00),
                new EmployeePayrollData("Mukesh",2,70000.00),
                new EmployeePayrollData("Anil",3,80000.00)
        };
        for (EmployeePayrollData employeePayrollData : data){
            Response response = addEmployeeToJSONServer(employeePayrollData);
            int statusCode = response.getStatusCode();
            Assertions.assertEquals(201,statusCode);

            employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
            employeePayrollService.addEmployeeToJSON(employeePayrollData);
        }
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        Assertions.assertEquals(11,entries);
    }

    @Test
    void givenNewSalaryForEmp_whenUpdateShouldMatch200Response() {
        EmployeePayrollService employeePayrollService;
        EmployeePayrollData[] payrollData = getEmployeeList();
        employeePayrollService = new EmployeePayrollService(Arrays.asList(payrollData));

        employeePayrollService.updateEmployeeSalary("Anil",90000.00, EmployeePayrollService.IOService.REST_IO);
        EmployeePayrollData employeePayrollData = employeePayrollService.getEmployeePayRollData("Anil");

        String empJson = new Gson().toJson(employeePayrollData);
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Content-Type","application/json");
        requestSpecification.body(empJson);
        Response response = requestSpecification.put("/employee_payroll/"+3);
        int statusCode = response.getStatusCode();
        Assertions.assertEquals(200,statusCode);
    }

    @Test
    void givenEmpToDelete_whenDelete_ShouldMatch200Response() {
        EmployeePayrollService employeePayrollService;
        EmployeePayrollData[] payrollData = getEmployeeList();
        employeePayrollService = new EmployeePayrollService(Arrays.asList(payrollData));

        EmployeePayrollData employeePayrollData = employeePayrollService.getEmployeePayRollData("Anil");
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Content-Type","application/json");
        Response response = requestSpecification.delete("/employee_payroll/"+employeePayrollData.employeeID);
        int statusCode = response.getStatusCode();
        Assertions.assertEquals(200,statusCode);

        employeePayrollService.deleteEmpFromJSON(employeePayrollData.employeeName, EmployeePayrollService.IOService.REST_IO);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        Assertions.assertEquals(4,entries);
    }
}


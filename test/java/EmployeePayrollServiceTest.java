import org.junit.Before;
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
        employeePayrollService.updateEmployeeSalary("TERISA", 30000000.00);
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
                new EmployeePayrollData("Jeff", 0, 1000000.00, "M", LocalDate.now()),
                new EmployeePayrollData("Bill", 0, 2000000.00, "M", LocalDate.now()),
                new EmployeePayrollData("Sunder", 0, 4000000.00, "M", LocalDate.now()),
                new EmployeePayrollData("Mukesh", 0, 44000000.00, "M", LocalDate.now()),
                new EmployeePayrollData("Anil", 0, 5000000.00, "M", LocalDate.now()),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmpPayRollData(EmployeePayrollService.IOService.DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployee(Arrays.asList(payrollData));
        Instant end = Instant.now();
        System.out.println("Duration with thread  "+Duration.between(start,end));
        Assertions.assertEquals(6,employeePayrollService.countEntries(EmployeePayrollService.IOService.DB_IO));
    }
}

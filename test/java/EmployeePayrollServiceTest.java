import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
        String date = "2018-01-01";
        String endDate= "2020-12-22";
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readFilteredEmpPayRollData(EmployeePayrollService.IOService.DB_IO,date,endDate);
        Assertions.assertEquals(3,employeePayrollData.size());
    }
}
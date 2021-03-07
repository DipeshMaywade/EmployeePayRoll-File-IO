import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {

    public enum IOService{CONSOLE_IO,FILE_IO,DB_IO,REST_IO}
    
    public List<EmployeePayrollData> employeePayrollList;

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList){
        this.employeePayrollList=employeePayrollList;
    }

    public void readData(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter Employee Name");
        String empName = scan.next();
        System.out.println("Enter Employee ID");
        int empID = scan.nextInt();
        System.out.println("Enter Employee Salary");
        int empSalary = scan.nextInt();

        EmployeePayrollData adder = new EmployeePayrollData(empName,empID,empSalary);
        employeePayrollList.add(adder);
    }

    public void empWriteData(IOService ioService){
        if (ioService.equals(IOService.CONSOLE_IO))
            System.out.println("OutPut\n"+employeePayrollList);
        else if (ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().writeData(employeePayrollList);
    }

    public void printData(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().printData();
        else
            System.out.println("Chose File_IO");
    }

    public long countEntries(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return 0;
    }

    public static void main(String[] args) {
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        //employeePayrollService.readData();
        employeePayrollService.empWriteData(IOService.FILE_IO);
    }
}
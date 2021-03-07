import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
    List<EmployeePayrollData> employeePayrollList;

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

    public void writeData(){
        System.out.println(employeePayrollList);
    }

    public static void main(String[] args) {
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        employeePayrollService.readData();
        employeePayrollService.writeData();
    }
}
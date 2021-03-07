public class EmployeePayrollData {
    String employeeName;
    int employeeID;
    int employeeSalary;

    public EmployeePayrollData(String employeeName, int employeeID, int employeeSalary) {
        this.employeeName = employeeName;
        this.employeeID = employeeID;
        this.employeeSalary = employeeSalary;
    }

    @Override
    public String toString() {
        return "employeeName='" + employeeName + '\'' + ", employeeID=" + employeeID + ", employeeSalary=" + employeeSalary;
    }
}
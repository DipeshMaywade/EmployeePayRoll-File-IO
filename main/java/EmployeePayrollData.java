import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {
    public String employeeName;
    public int employeeID;
    public double employeeSalary;
    public String gender;
    public LocalDate start;

    public EmployeePayrollData(String employeeName, int employeeID, double employeeSalary, String gender, LocalDate start) {
        this(employeeName,employeeID,employeeSalary);
        this.gender = gender;
        this.start = start;
    }

    public EmployeePayrollData(String employeeName, double employeeSalary) {
        this.employeeName = employeeName;
        this.employeeSalary = employeeSalary;
    }

    public EmployeePayrollData(String employeeName, int employeeID, double employeeSalary) {
        this.employeeName = employeeName;
        this.employeeID = employeeID;
        this.employeeSalary = employeeSalary;
    }

    @Override
    public String toString() {
        return "employeeName='" + employeeName + '\'' + ", employeeID=" + employeeID + ", employeeSalary=" + employeeSalary;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeName, employeeID, employeeSalary, gender, start);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return employeeID == that.employeeID && Double.compare(that.employeeSalary, employeeSalary) == 0 && Objects.equals(employeeName, that.employeeName) && Objects.equals(gender, that.gender) && Objects.equals(start, that.start);
    }
}
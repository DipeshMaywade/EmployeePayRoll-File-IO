import java.time.LocalDate;
import java.util.*;

public class EmployeePayrollService {

    public enum IOService{CONSOLE_IO,FILE_IO,DB_IO,REST_IO}

    private List<EmployeePayrollData> employeePayrollList;
    private EmployeePayrollDBService employeePayrollDBService;
    public EmployeePayrollService(){
        employeePayrollDBService=EmployeePayrollDBService.getInstance();
    }
    
    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList){
        this();
        this.employeePayrollList = new ArrayList<>(employeePayrollList);
    }

    public long readData(IOService ioService){
        if (ioService.equals(IOService.CONSOLE_IO)) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter Employee Name");
            String empName = scan.next();
            System.out.println("Enter Employee ID");
            int empID = scan.nextInt();
            System.out.println("Enter Employee Salary");
            int empSalary = scan.nextInt();

            EmployeePayrollData adder = new EmployeePayrollData(empName, empID, empSalary);
            employeePayrollList.add(adder);
            long result = employeePayrollList.size();
            return result;
        }else if(ioService.equals(IOService.FILE_IO)){
            this.employeePayrollList = new EmployeePayrollFileIOService().readData();
            return employeePayrollList.size();
        }else
            return 0;
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
            System.out.println(employeePayrollList);
    }

    public long countEntries(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return employeePayrollList.size();
    }

    public static void main(String[] args) {
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        employeePayrollService.empWriteData(IOService.FILE_IO);
        employeePayrollService.readData(IOService.FILE_IO);

    }

    public List<EmployeePayrollData> readEmpPayRollData(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            this.employeePayrollList = employeePayrollDBService.readData();
        return this.employeePayrollList;
    }

    public void updateEmployeeSalary(String name, double salary,IOService ioService) {
        if (ioService.equals(IOService.DB_IO)) {
            int result = employeePayrollDBService.updateEmployeeData(name, salary);
            if (result == 0) return;
        }
        EmployeePayrollData employeePayrollData = this.getEmployeePayRollData(name);
        if (employeePayrollData != null) employeePayrollData.employeeSalary= (int) salary;
    }

    public EmployeePayrollData getEmployeePayRollData(String name) {
        for (EmployeePayrollData data : employeePayrollList) {
            if (data.employeeName.equals(name))
                return data;
        }
        return null;
    }

    public boolean checkEmployeePayRollSyncWithDB(String name) {
        List<EmployeePayrollData>employeePayrollDataList= employeePayrollDBService.getEmployeePayRollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayRollData(name));
    }

    public List<EmployeePayrollData> readFilteredEmpPayRollData(IOService ioService, LocalDate date, LocalDate date1) {
        if (ioService.equals(IOService.DB_IO))
            this.employeePayrollList = employeePayrollDBService.readFilteredData(date,date1);
        return this.employeePayrollList;
    }

    public double sumOfSalary(IOService ioService, String gender) {
        if (ioService.equals(IOService.DB_IO)) {
            double result = employeePayrollDBService.sumOfSalary(gender);
            return result;
        }
        return 0.0;
    }

    public double avgOfSalary(IOService ioService, String gender) {
        if (ioService.equals(IOService.DB_IO)) {
            double result = employeePayrollDBService.avgOfSalary(gender);
            return result;
        }
        return 0.0;
    }

    public int countByGender(IOService ioService, String gender) {
        if (ioService.equals(IOService.DB_IO)) {
            int result = employeePayrollDBService.countByGender(gender);
            return result;
        }
        return 0;
    }

    public void addEmployee(String name, String gender, double salary, LocalDate date) {
        employeePayrollList.add(employeePayrollDBService.addEmployeeData(name,gender,salary,date));
    }

    public void addEmployeeToDB(List<EmployeePayrollData> employeePayrollDataList) {
        employeePayrollDataList.forEach(employeePayrollData ->{
            System.out.println("Emp Being Added: "+employeePayrollData.employeeName);
            this.addEmployee(employeePayrollData.employeeName,employeePayrollData.gender,employeePayrollData.employeeSalary,employeePayrollData.start);
            System.out.println("Emp Added: "+employeePayrollData.employeeName);
        });
        System.out.println(employeePayrollDataList);
    }

    public void addEmployeeToDBWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
        Map<Integer,Boolean> empAdditionStatus = new HashMap<Integer,Boolean>();
        employeePayrollDataList.forEach(employeePayrollData -> {
            Runnable task = () ->{
                empAdditionStatus.put(employeePayrollData.hashCode(),false);
                System.out.println("Employee Being Added : " + Thread.currentThread().getName());
                this.addEmployee(employeePayrollData.employeeName,employeePayrollData.gender,employeePayrollData.employeeSalary,employeePayrollData.start);
                empAdditionStatus.put(employeePayrollData.hashCode(),true);
                System.out.println("Employee Being Added : " + Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, employeePayrollData.employeeName);
            thread.start();
        });
        while (empAdditionStatus.containsValue(false)){
            try{Thread.sleep(10);
            }catch  (InterruptedException e){}
        }
        System.out.println(employeePayrollDataList);
    }

    public void addEmployeeToJSON(EmployeePayrollData employeePayrollData) {
        employeePayrollList.add(employeePayrollData);
    }

    public void deleteEmpFromJSON(String employeeName,IOService ioService) {
        if (ioService.equals(IOService.REST_IO)) {
            EmployeePayrollData employeePayrollData = this.getEmployeePayRollData(employeeName);
            employeePayrollList.remove(employeePayrollData);
        }
    }

}
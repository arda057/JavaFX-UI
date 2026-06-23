package JavaFXexample;

public class Student 
{
    private int id;
    private String name;
    private double gpa;
    private String department;

    public Student(int id, String name, double gpa, String department)
    {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
        this.department = department;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public double getGpa()
    {
        return gpa;
    }

    public String getDepartment()
    {
        return department;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setGpa(double gpa){
        this.gpa = gpa;
    }

    public void setDepartment(String department){
        this.department = department;
    }
}

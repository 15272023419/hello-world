public class Student {
    private String sName = "jack";
    private int age = 20;
    private String teacher = "zb";

    public Student() {
        System.out.println("无参构造方法!");
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}

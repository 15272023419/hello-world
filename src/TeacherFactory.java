public class TeacherFactory {
    public Teacher createTeacher(){
        Teacher teacher = new Teacher();
        System.out.println("TeacherFactory 负责创建  Teacher 实例对象------(动态)");
        return  teacher;
    }

    public static Teacher createTeacher2(){
        Teacher teacher = new Teacher();
        System.out.println("TeacherFactory 负责创建  Teacher 实例对象------(静态)");
        return  teacher;
    }
}

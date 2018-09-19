import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanFactory {

    private List<BeanDefined> beanDefineds;//存放bean的集合

    private Map<String , Object> springIoc;//存放已经创建好的实例对象(用于单例模式)


    public BeanFactory(List<BeanDefined> beanDefinedList) throws Exception {
        this.beanDefineds = beanDefinedList;
        springIoc = new HashMap<>();
        for(BeanDefined bean : beanDefineds){
            if("singleton".equals(bean.getScope())){
                String classPath = bean.getClassPath();
                Class classFile = Class.forName(classPath);
                Object instance = classFile.newInstance();
                springIoc.put(bean.getBeanId() , instance);
            }
        }
    }

    /**
     * 获取bean实例
     * @param beanId
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Object getBean(String beanId) throws Exception{
        Object instance = null;
        for(BeanDefined bean : beanDefineds){
            if(beanId.equals(bean.getBeanId())){
                String calsspath = bean.getClassPath();
                Class classfile = Class.forName(calsspath);
                String factoryBean = bean.getFactoryBean();
                String factoryMethod = bean.getFactoryMethod();
                if("prototype".equals(bean.getScope())){

                    /* *********** 动态工厂拓展 begin ************/
                    if(factoryBean != null && factoryMethod != null) {
                        Object factoryObj = springIoc.get(factoryBean);
                        instance = getBeanFactory(factoryObj , factoryMethod);
                    }
                    /* *********** 动态工厂拓展 end ************/

                    /* *********** 静态工厂拓展 begin ************/
                    else if(factoryBean == null && factoryMethod != null){
                        // 取出bean集合中的工厂bean对象，调用其静态方法
                        Object factoryObj = springIoc.get(beanId);
                        instance = getBeanFactory(factoryObj , factoryMethod);
                    }
                    /* *********** 静态工厂拓展 end ************/

                    else {
                        //如果scope是prototype（原型模式），每次都创建一个新的实例对象
                        //在spring中调用默认的构造方法，这里我们也调用默认的构造方法
                        instance = classfile.newInstance();
                    }
                }else{
                    //如果scope是singleton（单例模式），返回同一个实例对象
                    instance = springIoc.get(beanId);
                }
                return instance;
            }
        }
        return null;
    }

    /**
     * 1.动态的需要传入工厂代理的bean对象
     * 2.静态的需要
     * @param factoryObj
     * @param factoryMethod
     * @return
     * @throws Exception
     */
    public Object getBeanFactory(Object factoryObj , String factoryMethod) throws Exception {
        Class factoryClass = factoryObj.getClass();
        Method methodObj = factoryClass.getDeclaredMethod(factoryMethod, null);
        methodObj.setAccessible(true);
        Object instance = methodObj.invoke(factoryObj, null);
        return instance;
    }

    public static void main(String[] args) throws Exception {
        /**
         *  申明注册bean
         *  相当于Spring配置文件中添加的<bean>标签
         *  注册完之后准备纳入BeanFactory
         */
        BeanDefined beanDefined = new BeanDefined();
        beanDefined.setBeanId("teacher");
        beanDefined.setClassPath("Teacher");
        beanDefined.setScope("prototype");
        beanDefined.setFactoryBean("factory");
        beanDefined.setFactoryMethod("createTeacher2");
        /**
         * 获取工厂类
         */
        BeanDefined beanDefined1 = new BeanDefined();
        beanDefined1.setBeanId("factory");
        beanDefined1.setClassPath("TeacherFactory");
        List<BeanDefined> configuration = new ArrayList<>();
        configuration.add(beanDefined);
        configuration.add(beanDefined1);
        /**
         *
         * 声明一个BeanFactory,类似于Spring中的ApplicationContext
         * 如同bean添加到spring容器中一样
         */
        BeanFactory factory = new BeanFactory(configuration);

        Teacher teacher = (Teacher) factory.getBean("teacher");
        System.out.println("teacher = " + teacher);




        /**
         * 开发人员向BeanFactory索要实例对象
         * 通过BeanFactory来进行对象实例化
         *//*
        Student student1 = (Student)factory.getBean("student");
        System.out.println(student1);
        Student student2 = (Student)factory.getBean("student");
        System.out.println(student2);

        //反射注入值
        String classPath = "Teacher";
        Class fileClassPath = Class.forName(classPath);
        Teacher teacher = (Teacher) fileClassPath.newInstance();;//通过反射创建对象
        Field f = fileClassPath.getDeclaredField("name");// 调用getDeclaredField("name") 取得name属性对应的Field对象
        f.setAccessible(true);// 取消属性的访问权限控制，即使private属性也可以进行访问。
        // 调用get()方法取得对应属性值。
        System.out.println(f.get(teacher));  //相当于obj.getName();
        f.set(teacher , "我是老师");// 调用set()方法给对应属性赋值。
        Field f2 = fileClassPath.getDeclaredField("age");
        f2.setAccessible(true);
        f2.set(teacher , 20);
        System.out.println(teacher);*/
    }
}

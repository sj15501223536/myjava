package Annotation;

import java.lang.annotation.Documented;  
import java.lang.annotation.ElementType;  
import java.lang.annotation.Inherited;  
import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.Target;  
import java.net.Authenticator.RequestorType;  
  
/** 
 * 自定义注解 
 * @author Uno 
 *@Documented:指明该注解可以用于生成doc 
 *@Inherited：该注解可以被自动继承 
 *@Retention:指明在什么级别显示该注解： 
 *  RetentionPolicy.SOURCE 注解存在于源代码中，编译时会被抛弃 
    RetentionPolicy.CLASS 注解会被编译到class文件中，但是JVM会忽略 
    RetentionPolicy.RUNTIME JVM会读取注解，同时会保存到class文件中 
  @Target:指明该注解可以注解的程序范围 
    ElementType.TYPE 用于类，接口，枚举但不能是注解 
    ElementType.FIELD 作用于字段，包含枚举值 
    ElementType.METHOD 作用于方法，不包含构造方法 
    ElementType.PARAMETER 作用于方法的参数 
    ElementType.CONSTRUCTOR 作用于构造方法 
    ElementType.LOCAL_VERIABLE 作用于本地变量或者catch语句 
    ElementType.ANNOTATION_TYPE 作用于注解 
    ElementType.PACKAGE 作用于包 
 */  
@Documented  
@Inherited  
@Retention(RetentionPolicy.RUNTIME)  
@Target({ElementType.TYPE, ElementType.FIELD})//次注解作用于类和字段上  
public @interface FieldTypeAnnotation {  
    /** 
     *leip 2016年12月3日 
     *TODO 
    **/  
    String type() default "ignore";  
    int age() default 27;  
    String[] hobby(); //没有指定defalut的，需要在注解的时候显式指明  
}  
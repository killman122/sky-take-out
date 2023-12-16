package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     * <p>
     * 通过注解的方式编写sql, 并且这里使用el表达式的方式向参数中传入对应的数值, 这里传入的数值就是接收到的username用户名, 这里对于mybatis中的#{}和${}的使用
     * 在使用mybatis对数据库中的数据进行操作的时候, 可以使用注解的方式进行查询, 也可以使用xml配置文件的方式进行查询
     * 对于简单并且少量的sql查询, 可以直接使用mybatis中注解的方式进行查询, 但是对于复杂并且大量的sql查询, 推荐使用xml配置文件的方式进行查询
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据
     * 在插入的时候, 直接传入实体类对象, 然后在sql语句中使用el表达式的方式获取实体类对象中的属性值, 并通过执行sql语句将数据保存到数据库中
     * @param employee
     */
    @Insert("insert into employee(name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user)" +
            "values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Employee employee);//这里对于mybatis的配置可以使用注解的方式也可以使用xml, 使用xml直接通过alt+insert进行填充即可
}
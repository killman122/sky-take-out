package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

import static com.sky.constant.PasswordConstant.DEFAULT_PASSWORD;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     * 将登录的业务逻辑抽取到service层中，这样做的好处是，controller层中的代码更加简洁，而且controller层中的代码更加容易维护
     * 在service层中，可以对数据进行处理，比如对密码进行加密，然后再进行比对
     * 将EmployeeService接口中的抽象方法进行实现在实现类EmployeeServiceImpl中
     * 在实现类中，可以调用mapper层中的方法，也可以调用其他的service层中的方法
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);//这里的username就是接收到的username用户名

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端的明文密码进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());//这里传入的参数需要是一个byte数组或者是直接传入一个InputStream输入流, 由于传入的是一个byte[]数组, 所以需要将字符串转换为byte二进制byte类型数组, 这里使用的是getBytes()方法将字符串转换为byte二进制byte类型数据, 差点忘了new Scanner(System.in)的方式创建用户输入(扫描的方式)
        if (!password.equals(employee.getPassword())) {//由于数据库中的用户数据是已经使用md5加密过的, 所以这里需要将用户输入的密码也进行md5加密, 然后再进行比对
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }


        /*if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }*/

        //在检查密码是否正常后, 检查账号是否被锁定, 如果账号被锁定, 也就是status字段的值为0(禁用状态), 则抛出账号被锁定异常
        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * 注意在重写方法时的@Override注解, 这个注解是用来检查当前方法是否是重写方法, 不是必须存在的, 但是如果存在的话, 则会检查当前方法是否是重写方法
     * 如果当前方法是重写方法, 则会检查当前方法的方法名是否和父类中的方法名一致, 如果一致, 则不会报错, 如果不一致, 则会报错
     * 调用持久层的mapper将数据保存到数据库中, 注意在持久层mapper中需要使用sql语句(使用注解的方式), 当然也可以使用xml配置文件的方式
     * 传递给持久层mapper到数据库中的数据, 建议使用实体类, 使用DTO中的属性没有使用实体类的属性全, 因为DTO中的只是从前端表单中传递过来的数据, 但是实际上的数据库中的数据可能会更多, 所以建议使用实体类
     *
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //employee.setUsername(employeeDTO.getUsername());//从DTO中获取实体对象的属性并将属性设置到实体类中, 但是由于逐行逐个设置属性较慢, 所以可以使用对象属性拷贝的方式将DTO对象中的属性赋值给Entry实体对象属性中

        //使用对象属性拷贝的方式将DTO对象中的属性赋值给Entry实体对象属性中, 在拷贝属性值的时候, 如果源对象中的属性值为null, 则不会拷贝到目标对象中, 如果源对象中的属性值不为null, 则会拷贝到目标对象中
        BeanUtils.copyProperties(employeeDTO, employee);//这里的employeeDTO是源对象, employee是目标对象, 这里的源对象中的属性值会被拷贝到目标对象中, 这里的属性值是根据属性名进行拷贝的, 源对象中的属性名和目标对象中的属性名要一致, 如果不一致, 则不会拷贝

        //设置处理DTO中的属性外的属性

        //设置账号状态, 默认情况下是正常的状态, 也就是ENABLE启用状态, 不直接使用赋值参数的硬编码方式, 而是使用常量类中的常量值, 这样做的好处是, 如果常量值发生改变, 则不需要修改代码, 只需要修改常量类中的常量值即可
        employee.setStatus(StatusConstant.ENABLE);

        //设置密码, 使用默认密码, 也就是123456, 这里的密码需要使用md5加密, 这里使用的是spring框架中的工具类DigestUtils, 这个工具类中的md5加密方法需要传入一个byte[]数组或者是直接传入一个InputStream输入流, 由于传入的是一个byte[]数组, 所以需要将字符串转换为byte二进制byte类型数组, 这里使用的是getBytes()方法将字符串转换为byte二进制byte类型数据
        employee.setPassword(DigestUtils.md5DigestAsHex(DEFAULT_PASSWORD.getBytes()));

        //设置当前记录的创建时间和修改时间, 这里的创建时间和修改时间都是当前时间, 所以可以直接使用new Date()获取当前系统时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置当前记录的创建人和修改人, 这里的创建人和修改人都是当前登录的用户, 所以可以直接使用SecurityUtil工具类中的getLoginUser()方法获取当前登录的用户
        //employee.setCreateUser(10L);
        //从JwtTokenAdminInterceptor拦截器中获取当前登录用户的id并存储到ThreadLocal中, 这里的ThreadLocal是一个线程安全的容器, 用来存储当前线程中的数据, 这里的ThreadLocal中的数据只能在当前线程中获取, 不能跨线程获取, 这里的ThreadLocal中的数据是线程隔离的, 也就是说, 当前线程中的数据不能被其他线程获取, 这里的ThreadLocal中的数据是线程共享的, 也就是说, 当前线程中的数据可以被当前线程中的其他方法获取, 这里的ThreadLocal中的数据是线程独立的, 也就是说, 当前线程中的数据不能被其他线程修改, 这里的ThreadLocal中的数据是线程共享的, 也就是说, 当前线程中的数据可以被当前线程中的其他方法修改
        employee.setCreateUser(BaseContext.getCurrentId());//service层通过调用去除相关的雇员id
        //employee.setUpdateUser(10L);
        employee.setUpdateUser(BaseContext.getCurrentId());

        //调用注入(自动装配的)持久层mapper将数据保存到数据库中
        employeeMapper.insert(employee);

    }

}

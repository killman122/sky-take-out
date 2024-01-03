package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")//使用swagger的注解@Api，表示这个类是一个swagger的配置类, 传入的参数tags属性描述当前类的作用
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;//通过自动装配的方式创建出一个bean对象, 实质上这里的bean对象就是EmployeeServiceImpl类的实例对象, 但是这里的bean对象是通过接口EmployeeService来创建的, 所以这里的bean对象是EmployeeService接口的实现类EmployeeServiceImpl类的实例对象
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录", notes = "员工登录")
    //使用swagger的注解@ApiOperation，表示这个方法是一个swagger的配置方法, 传入的参数value属性表示当前方法的作用, notes属性表示当前方法的备注说明
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {//这里使用@RequestBody表示通过请求体以json的格式提交数据
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);//在调用login的时候, 实质是调用的实现类中的login方法

        //登录成功后，生成jwt令牌, 在Controller层中生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());//这里向Map双例集合中传入令牌常量和对应的值, 这里的值value就是employee.getId(), key从常量类JwtClaimsConstant中获取常量员工id--EMP_ID
        String token = JwtUtil.createJWT(// 通过JwtUtil工具类中的createJWT方法生成jwt令牌token, 但是需要注意这里的JwtUtil工具类也是自写的,不过这里生成加密的方式也可以使用python进行处理
                jwtProperties.getAdminSecretKey(),//传入令牌的key
                jwtProperties.getAdminTtl(),//传入令牌的过期时间
                claims);

        //由于在VO类中使用了lombok插件开启注解模式并使用注解@Builder, 所以这里可以使用builder构建器模式, 不用在new出对象或者使用bean对象在调用set方法设置属性值
        //并且这里的builder构建器模式中的属性值是按照VO类中的属性顺序进行设置的, 所以这里的builder构建器模式中的属性值的顺序要和VO类中的属性顺序一致
        //构建器主要使用场景是在创建对象时有很多参数需要设置, 并且有些参数是可选的, 有些是必须的, 这时候就可以使用构建器模式
        //构建器的方式中需要开启lombok插件的注解模式, 并且在VO类中使用注解@Builder, 这样就可以使用builder构建器模式
        //使用方式为: VO类名.builder().属性1(属性1的值).属性2(属性2的值).属性3(属性3的值).build();
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * <p>
     * 使用Post请求方式, 传递的表单数据是一个json类型数据, 所以需要使用@RequestBody注解将json类型数据转换为java对象
     * 使用参数接收前端表单中传递过来的数据, 这里的参数可以使用实体类接收, 也可以使用Map集合接收, 这里使用实体类DTO接收
     * DTO: Data Transfer Object, 数据传输对象, 用于封装业务层传递的数据, 一般用于业务层和控制层之间的数据传递
     * <p>
     * 由于使用Post请求方式, 所以在Spring中使用@PostMapping注解, 传递的参数是一个路径, 这里的路径是/admin/employee,
     * 由于在Controller类的外层使用@RequestMapping注解, 所以这里的路径是/admin/employee不需要再将路径参数添加到@PostMapping注解中
     * <p>
     * 使用Swagger注解@ApiOperation为了在集中测试时的便利，表示这个方法是一个swagger的配置方法, 传入的参数value属性表示当前方法的作用, notes属性表示当前方法的备注说明
     *
     * @param employeeDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);//使用占位符{}, 将逗号后的参数动态的添加到占位符中
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 按页查询员工信息, 员工分页查询
     *
     * @param employeePageQueryDTO
     * @return <p>
     * 使用get请求的方式进行查询, 所以在Spring中使用@GetMapping注解, 并且查询中传入的是请求参数Query而不是json数据用作查询的请求体, 所以在传入的参数中也不需要使用@RequestBody注解
     * Controller层调用Service接口, Service接口调用Mapper接口, Mapper接口调用Mapper.xml配置文件, Mapper.xml配置文件中使用sql语句进行查询
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询：参数为{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用或禁用员工
     * <p>
     * 对于查询的请求方法, 在使用的时候需要加上泛型的限定, 对于非查询类的请求, 在构造方法的时候是不需要加上泛型的限定的
     * 传入的参数有两个, 一个参数是路径参数path, 也就是描述当前请求的status, 另一个参数是请求参数Query, 也就是描述当前请求的查询参数(员工id)
     * 由于请求方式是Post请求方式, 所以需要在方法前加入一个@PostMapping注解, 由于请求参数是一个路径参数, 所以需要在参数前加入一个@PathVariable注解, 由于请求参数是一个查询参数, 所以需要在参数前加入一个@RequestBody注解
     * 使用{}来接收路径参数, 使用@PathVariable注解来接收路径参数@PathVariable("status") Integer status, 使用@RequestBody注解来接收查询参数
     * 如果路径参数和Mapping注解中的参数一致, 那么就不需要在@PathVariable注解中添加参数, 如果不一致, 那么就需要在@PathVariable注解中添加参数
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用或禁用员工账号")
    //public Result startOrStop(@PathVariable Integer status, Long id) {//注意在使用路径参数的时候需要在参数前加一个路径变量的注解
    public Result startOrStop(@PathVariable("status") Integer status, Long id) {//注意在使用路径参数的时候需要在参数前加一个路径变量的注解
        log.info("启用或禁用员工账号：status={}, id={}", status, id);
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id查询员工信息
     * <p>
     * 使用@PathVariable注解来接收路径参数, 并将路径参数传递到请求路径中, 由于路径中的参数名和注解中的参数名一致, 所以不需要在注解中添加参数, 否则需要指定路径参数的名称
     * <p>
     * 需要注意的是为了实现统一处理, 在从数据库中查询数据的时候, 需要将查询到的数据封装到一个实体类对象中, 然后将实体类对象返回, 这样在Controller层中就可以直接调用实体类对象中的get方法获取实体类对象中的属性值
     * <p>
     * 并且在获取到实体的所有属性值后, 具体选取实体中的哪个属性值取决于前端页面中某个方法方法或者变量组件的获取, 而不是从后端中独立的获取某个属性值
     * @param id
     * @return 返回的是一个Result类型的数据, 观察文档可以发现, 这里需要返回的data是一个实体类对象, 所以这里的返回值类型是Result<Employee>.success 不能直接但会无泛型的success调用
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工信息")
    public Result<Employee> getById(@PathVariable Long id) {
        //这里调用后返回一个实体类对象, 因为最终要从实体类对象中调用get方法获取实体类对象中的属性
        log.info("根据员工id查询员工信息：id={}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 编辑员工信息--由于是修改不是获取数据,使用put请求方式
     * 在返回的时候只需要返回指定的数据, 不需要返回所有的数据, 所以这里的返回值类型是Result.success
     * 注意在使用put请求方式的时候, 传递的参数是一个json类型的数据, 所以需要使用@RequestBody注解将json类型的数据转换为java对象, 否则在调用时传入的对象为null, 属性也为null
     * @param employeeDTO
     * @return
     */
    @PutMapping()
    @ApiOperation("编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){//在从参数中获取数据时是从DTO也就是不完全的对象中获取数据而不是从完整的实体类对象中获取数据, 这样做的目的是为了防止在从前端获取数据时, 从前端获取到的数据中包含了一些不需要的数据, 比如说员工的密码等等
        log.info("编辑员工信息{}方法被调用", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}

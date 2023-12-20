package com.sky.controller.admin;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 * 使用@RestController注解表示这是一个控制器, 并且其中的方法都是以json格式进行响应
 * 通过MultipartFile参数接口类型的接收二进制文件, 这里的参数名file需要和前端提交的参数名一致, MultipartFile file 理解为一个已经被 Spring 初始化并包含了上传文件数据的对象
 * 这个MultipartFile参数不是多态的体现。多态在Java中是指一个引用变量到底会指向哪个类的实例对象，是由程序运行期间动态绑定决定的。在你的代码中，MultipartFile是一个接口，而file是实现了这个接口的某个类的对象。这里并没有体现出多态，因为我们并没有看到一个父类引用指向不同子类对象的情况。
 * file的实际类型是在运行时确定的，也就是当有客户端向你的API上传文件时，Spring会为你创建一个实现了MultipartFile接口的对象。
 * 返回的Result数据类型设置为String类型, 用于返回文件的访问路径, 在接口中使用data作为返回的路径参数
 */
@RestController
@RequestMapping("admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    //通过依赖注入的方式将创建的阿里云OSS工具类对象注入到当前类中, 这里注入的是OssConfiguration配置类中的aliOssUtil方法创建的Bean对象
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){//上传后的文件都是通过二进制数据存储在服务器中的, 所以这里的参数类型是MultipartFile, 用于接收二进制文件
        log.info("文件上传：{}", file.getOriginalFilename());

        try {
            //获取上传文件的原始文件名
            String originalFilename = file.getOriginalFilename();

            //截取原始文件名的后缀, 将png, jpg, gif等文件的后缀截取出来, 注意在Java中的sub方法截取时的索引似乎是包括了整个边界, 但是在python中一般不会包括边界的范围
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            //构造新文件名称主要是使用UUID和原有的后缀名进行拼接, 为了防止在上传的文件后出现重名文件的覆盖, 所以使用了UUID工具类生成一个随机的文件名, 使用String类中的toString方法将UUID对象转换为String类型
            String objectName = UUID.randomUUID().toString() + extension;

            //调用阿里云OSS工具类中的upload方法进行文件上传, 传入的参数是文件的二进制数据和文件名, 返回的是文件的访问路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);

            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}

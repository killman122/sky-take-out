package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import io.swagger.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //注入微信的appid和appsecret类
    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;


    //微信服务接口地址, 可以获取openid
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 调用微信接口服务获取微信用户的openid
     *
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        //调用微信的接口提交code获得返回的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);//小程序传入的code,并将code返回获取后通过请求获取到openid
        map.put("grant_type", "authorization_code");

        //请求微信官网获得到的是一个返回的json数据包, 里面包含了openid,session_key,unionid等信息
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        //String parse = (String) JSON.parse(json);
        //log.info("微信登录返回的json数据:{}", parse);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空, 如果openid为空则表示登录失败, 抛出业务异常
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //根据openid查询数据库中是否存在该用户, 如果不在则表示是新用户, 需要将该用户保存到数据库中
        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            //如果user为空, 则表示是新用户, 需要将该用户保存到数据库中, 使用构建器的方式向实体对象中增加属性
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        /*@Override
        public User wxLogin(UserLoginDTO userLoginDTO) {
            String openid = getOpenid(userLoginDTO.getCode());

            if (openid == null) {
                throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
            }

            List<User> users = userMapper.getByOpenid(openid);// 使用单例集合返回查询到的用户信息, 如果在存储的时候存储了多个则抛出异常
            if (users.isEmpty()) {
                User user = User.builder()
                        .openid(openid)
                        .createTime(LocalDateTime.now())
                        .build();
                userMapper.insert(user);
                return user;
            } else if (users.size() > 1) {
                throw new RuntimeException("Found more than one user with the same openid: " + openid);
            } else {
                return users.get(0);
            }
        }*/

        return user;
    }

}

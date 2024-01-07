package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShopCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "C端购物车相关接口")
public class ShoppingCartController {
    //开启自动装配
    @Autowired
    private ShopCartService shopCartService;

    /**
     * 添加购物车
     * <p>
     * 由于在返回数据市不需要返回data等相关字段所有在这里直接使用Result不加上泛型的限定, 并且接口中限定的请求体是一个json数据, 所以需要使用@RequestBody注解
     * @param shoppingCartDTO
     * @return
     *
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车: 商品信息为{}", shoppingCartDTO);
        shopCartService.addShopCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shopCartService.showShoppingCart();
        return Result.success(list);
    }
}

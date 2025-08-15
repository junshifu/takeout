package com.junda.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junda.reggie.common.R;
import com.junda.reggie.entity.User;
import com.junda.reggie.service.UserService;
import com.junda.reggie.utils.SMSUtils;
import com.junda.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            //生成验证码
            String core = ValidateCodeUtils.generateValidateCode4String(4).toString();
            log.info("code={}",core);
            //调用短信服务
            /*但是我没有阿里云的短信服务
            SMSUtils.sendMessage("瑞吉外卖","",phone,core);*/

            //将生成的验证码保存到Session
            session.setAttribute(phone,core);
            return R.success("手机验证码发送成功");
        }
        return R.success("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();

        //比对验证码
        Object codeSession = session.getAttribute(phone);
        if (codeSession != null && codeSession.equals(code)){
            //比对成功，登录放行

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            //判断是否新用户，如果是新用户则自动注册
            User user = userService.getOne(queryWrapper);
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);

        }
        return R.error("登录失败");
    }


    /**
     * 用户退出登录
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginOut(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }


}

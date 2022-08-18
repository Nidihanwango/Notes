package com.example.ppt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ppt.bean.User;
import com.example.ppt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Value("${hope.unitNo}")
    private String UnitNO;
    @Autowired
    private UserService userService;

    @PostMapping("/user")
    String registUser(User user, @RequestParam("companyCode") String code, Model model){
        if (!UnitNO.equals(code)){
            model.addAttribute("msg","单位编码错误,请重新输入");
            return "user/regist";
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,user.getUsername());
        if (userService.getOne(wrapper) != null){
            model.addAttribute("msg","用户名重复,请重新输入");
            return "user/regist";
        }
        userService.save(user);
        return "redirect:/user/regist_success";
    }

    @PutMapping("/user")
    String loginUser(User user, Model model, HttpSession session){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,user.getUsername()).eq(User::getPassword,user.getPassword());
        User one = userService.getOne(wrapper);
        if (one == null){
            model.addAttribute("loginError","用户名或密码错误");
            return "user/login";
        }
        session.setAttribute("user",one);
        return "redirect:/user/login_success";
    }

    @GetMapping("/user")
    String logoutUser(HttpSession session){
        session.removeAttribute("user");
        return "redirect:/index";
    }
}

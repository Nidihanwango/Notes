package com.example.ppt.controller;

import com.example.ppt.bean.Doctor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @RequestMapping({"/","/index"})
    String index(){
        return "client/index";
    }

    @RequestMapping("/user/regist")
    String regist(){
        return "user/regist";
    }

    @RequestMapping("/user/regist_success")
    String registSuccess(){
        return "user/regist_success";
    }

    @RequestMapping("/user/login")
    String login(){
        return "user/login";
    }

    @RequestMapping("/user/login_success")
    String loginSuccess(){
        return "user/login_success";
    }

    @RequestMapping("/manager")
    String manager(){
        return "manager/manager";
    }

    @RequestMapping("/manager/addDoctor/{type}")
    String addDoctor(@PathVariable("type") Integer type){
        return "manager/addDoctor";
    }

    @RequestMapping("/introduction/ppt")
    String show(){
        return "introduction/ppt";
    }

}

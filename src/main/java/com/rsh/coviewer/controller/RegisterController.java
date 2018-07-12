package com.rsh.coviewer.controller;

import com.rsh.coviewer.pojo.UserInformation;
import com.rsh.coviewer.pojo.UserPassword;
import com.rsh.coviewer.service.UserInformationService;
import com.rsh.coviewer.service.UserPasswordService;
import com.rsh.coviewer.token.TokenProccessor;
import com.rsh.coviewer.tool.Tool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by wsk1103 on 2017/4/30.
 */
@Controller
public class RegisterController {

    @Resource
    private UserInformationService userInformationService;

    @Resource
    private UserPasswordService userPasswordService;

    //the first step for register
    @RequestMapping(value = "register",method = {RequestMethod.POST, RequestMethod.GET})
    public String register(Model model, HttpServletRequest request) {
        String token = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("register1_token", token);
        model.addAttribute("token", token);
        String result = Tool.getInstance().getProtocolTxt();
        model.addAttribute("txt", result);
        request.getSession().setAttribute("rand","7834");
        model.addAttribute("random", "7834");
        return "registered/register";
    }

    //the second step for the register
    @RequestMapping(value = "register2",method = {RequestMethod.POST, RequestMethod.GET})
    public String register(@RequestParam String photo, @RequestParam String code_phone, @RequestParam String token,
                           Model model, HttpServletRequest request) {
        boolean isRepeatSubmit = RepeatSubmit.isRepeatSubmit(token, (String) request.getSession().getAttribute("register1_token"));
        if (isRepeatSubmit) {
            return register(model, request);
        } else {
            request.getSession().removeAttribute("register1_token");
        }
        String phone = (String) request.getSession().getAttribute("phone");
        String register2_token = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("register2_token", register2_token);
        request.getSession().setAttribute("phone", phone);
        model.addAttribute("token", register2_token);
        model.addAttribute("phone", phone);
        model.addAttribute("photo", photo);
        model.addAttribute("code_phone", code_phone);
        return "registered/register2";
    }

    //the end step for the register
    @RequestMapping(value = "register3",method = {RequestMethod.POST, RequestMethod.GET})
    public String register(@RequestParam String password,
                           HttpServletRequest request, Model model) {
        System.out.println("the end stop for the register");
        String token = TokenProccessor.getInstance().makeToken();
        String phone = (String) request.getSession().getAttribute("phone");
        request.getSession().setAttribute("token", token);
        model.addAttribute("token", token);
        model.addAttribute("phone", phone);
        model.addAttribute("password", password);
        return "registered/register3";
    }

    //fill in the password
    @RequestMapping(value = "fillPassword",method = {RequestMethod.POST, RequestMethod.GET})
    public String fillPassword(@RequestParam String password, @RequestParam String token, @RequestParam String username,
                               Model model, HttpServletRequest request) {
        System.out.println("fill in the password");
        String phone = (String) request.getSession().getAttribute("phone");
        System.out.println("phone:"+phone);
        boolean isRepeatSubmit = RepeatSubmit.isRepeatSubmit(token, (String) request.getSession().getAttribute("register2_token"));
        System.out.println("isRepeatSubmit:"+isRepeatSubmit);
        if (isRepeatSubmit) {
            return register(model, request);
        } else {
            request.getSession().removeAttribute("forget2_token");
        }
        if (phone.length() < 6 || phone.length() > 11) {
            return "redirect:/register";
        }
        if (password.length() < 6 || password.length() > 18) {
            return "redirect:/register";
        }
        try {
            UserInformation userInformation = new UserInformation();
            userInformation.setName(username);
            userInformation.setPhone(phone);
            userInformation.setModified(new Date());
            userInformation.setBuildtime(new Date());
            userInformation.setAvatar("/images/wuqiong.jpg");
            int id_userinformation = userInformationService.insertSelective(userInformation);
            System.out.println("id_userinformation:"+id_userinformation);
            int uid = getUserInformationId(phone);
            System.out.println("uid:"+uid);
            if (id_userinformation !=1){
                try {
                    userInformationService.deleteByPrimaryKey(uid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String md5Password = Tool.getInstance().getMD5(password);
            System.out.println("md5password:"+md5Password);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            UserPassword userPassword = new UserPassword();
            userPassword.setId(getUserInformationId(phone));
            userPassword.setPassword(md5Password);
            userPassword.setAllow((short) 1);
            userPassword.setModified(new Date());
            int id = userPasswordService.insertUser(userPassword);
            if (id == 0 || id_userinformation == 0) {
                return register( "", "", token, model, request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return register( "", "", token, model, request);
        }


        return register( password, request, model);
    }

//    //get the wsk`s protocol(协议)
//    @RequestMapping(value = "getProtocol",method = {RequestMethod.POST,RequestMethod.GET})
//    @ResponseBody
//    public Map getProtocol(){
//        Map<String, String> map = new HashMap<>();
//        Tool handleFile = new HandleFileImpl();
//        String result = handleFile.readFile("txt/wsk_protocol.txt");
//        map.put("result", result);
//        return map;
//    }

    private int getUserInformationId(String phone) {
        int id = 0;
        try {
            UserInformation userInformation = this.userInformationService.selectIdByPhone(phone);
            id = userInformation.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
}

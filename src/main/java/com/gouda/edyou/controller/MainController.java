package com.gouda.edyou.controller;

import com.gouda.edyou.entity.User;
import com.gouda.edyou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class MainController {
    private final UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = {"/", "/index", "/home"})
    public String home(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("name", principal.getName());
        }
        return "home";
    }
}

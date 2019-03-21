package com.nc.portal.controller;

import com.nc.portal.model.UserDTO;
import com.nc.portal.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/reg")
public class RegController {

    @Autowired
    AuthService authService;

    @GetMapping
    public String getReg(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "registration";
    }

    @PostMapping
    public String submit(@ModelAttribute UserDTO userDTO, Model model) {
        authService.createUser(userDTO);
        System.out.println("UserDTO created: " + userDTO.getUsername());
        //model.addAttribute("userDTO", userDTO);
        return "customer";
    }

}

package com.wms.ui.example.controller;

import com.wms.user.domain.LogRepo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//@RestController
@Controller
public class Example {
    @RequestMapping("/login")
    String login(){
        return "login";
    }
}
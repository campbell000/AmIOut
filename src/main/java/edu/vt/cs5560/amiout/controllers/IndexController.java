package edu.vt.cs5560.amiout.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController
{
    @RequestMapping("/")
    public String loadIndexPage()
    {
        return "index";
    }
}

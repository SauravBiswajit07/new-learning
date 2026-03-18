package com.example.securitydemo;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreeetingsController {

    @GetMapping("/hello")
    public String greet(){

        return "Helloooo World";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String user(){

        return "Helloooo user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin(){

        return "Helloooo admin";
    }
}

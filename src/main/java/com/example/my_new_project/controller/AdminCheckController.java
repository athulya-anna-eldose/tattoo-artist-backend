package com.example.my_new_project.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminCheckController {

    // AdminKeyFilter already returns 403 for a wrong key,
    // so reaching this method means the key is correct.
    @PostMapping("/api/admin/check")
    public void check() { }
}
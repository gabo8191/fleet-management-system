package com.translogistics.fleetmanagmentsystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class CustomErrorController implements ErrorController {
    @GetMapping("/error")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleError(HttpServletRequest request) {
        return "error/404";
    }
}

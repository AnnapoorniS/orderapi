package com.retail.orderapi.controller;

import com.retail.orderapi.auth.LoginService;
import com.retail.orderapi.models.UserCredentials;
import com.retail.orderapi.models.UserTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("login")
@Validated
public class LoginController {

    @Autowired
    LoginService loginService;

    @PostMapping
    ResponseEntity<UserTokenResponse> placeOrder(@Valid @RequestBody UserCredentials userCredentials) {
        UserTokenResponse userTokenResponse = loginService.loginUser(userCredentials);
        return ResponseEntity.ok().body(userTokenResponse);
    }
}

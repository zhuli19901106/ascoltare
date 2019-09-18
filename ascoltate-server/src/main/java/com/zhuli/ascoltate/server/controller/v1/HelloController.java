package com.zhuli.ascoltate.server.controller.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuli.ascoltate.common.dto.response.Response;

@RequestMapping(value = "/ascoltate/v1/hello", produces = "application/json")
@RestController
public class HelloController {
    @GetMapping
    public ResponseEntity<Response<String>> hello() {
        return ResponseEntity.ok(new Response<>("Hello world."));
    }
}

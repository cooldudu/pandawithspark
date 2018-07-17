package com.wms.ui.example.controller;

import com.wms.core.annotation.MakeLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/manage")
public class Example2 {

    @GetMapping("/index")
    @MakeLog(logContent="have test ${principal.getName} split ${exchange.getRequest.getHeaders.getHost.getHostName}")
    public Mono<String> hello(ServerWebExchange exchange, Principal principal) {
        //new UserRepo().findEntityById(1).map(r -> Json.toJson(r.get(),UserRepo.userFormat())).subscribe(System.out::println);
        //new UserRepo().countEntity().subscribe(System.out::println);
        return Mono.just(principal.getName());
        //return new UserRepo().findIdByUserNameWithExec("user4").collectList();
    }
}
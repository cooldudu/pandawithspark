package com.wms.ui.example.controller;

import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/manage")
public class Example1 {
    @GetMapping("/index")
    public Mono<String> hello(ServerWebExchange exchange, Principal principal) {
        //new UserRepo().findEntityById(1).map(r -> Json.toJson(r.get(),UserRepo.userFormat())).subscribe(System.out::println);
        //new UserRepo().countEntity().subscribe(System.out::println);
        LogFactory.getLog(Example1.class).info("I came into the manage page.");
        return Mono.just("hello");//principal.map( p ->p.getName());
        //return new UserRepo().findIdByUserNameWithExec("user4").collectList();
    }
//    String index() {
//        return new Test().abc();
//    }

//    @GetMapping("/principal")
//    public String principal(Principal principal) {
//        return "Hello " + principal.getName();
//    }
//
//    @GetMapping("/principal/mono")
//    public Mono<String> principalMono(Mono<Principal> principal) {
//        return principal.map( p -> "Hello " + p.getName());
//    }
//
//    @GetMapping("/userdetails")
//    public String principal(@AuthenticationPrincipal UserDetails userDetails) {
//        return "Hello " + userDetails.getName();
//    }
//
//    @GetMapping("/userdetails/mono")
//    public Mono<String> userDetailsMono(@AuthenticationPrincipal Mono<UserDetails> userDetails) {
//        return userDetails.map( p -> "Hello " + p.getName());
//    }
//
//    @GetMapping("/exchange/principal")
//    public Mono<String> exchangePrincipal(ServerWebExchange exchange) {
//        return exchange.getPrincipal().map( p -> "Hello " + p.getName());
//    }
}
package com.wms.ui.example.controller;

import com.wms.core.annotation.MakeLog;
import com.wms.core.annotation.Token;
import com.wms.core.annotation.UseSparkSession;
import com.wms.core.ui.AbstractController;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/manage")
public class Example2 extends AbstractController {

    @GetMapping("/index")
    @MakeLog(logContent="have test ${principal.getName} split ${exchange.getRequest.getHeaders.getHost.getHostName}")
    //@Token
    @UseSparkSession
    public Mono<String> hello(ServerWebExchange exchange, Principal principal) {
        return Mono.just(principal.getName());
    }
}
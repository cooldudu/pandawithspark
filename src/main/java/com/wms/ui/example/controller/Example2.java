package com.wms.ui.example.controller;

import com.wms.core.annotation.MakeLog;
import com.wms.core.annotation.Token;
import com.wms.core.annotation.UseSparkSession;
import com.wms.core.annotation.UseSparkStreamingContext;
import com.wms.core.ui.AbstractController;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import scala.Tuple2;

import java.security.Principal;
import java.util.Arrays;

@RestController
@RequestMapping("/console")
public class Example2 extends AbstractController {

    @GetMapping("/index")
    @MakeLog(logContent="have test ${principal.getName} split ${exchange.getRequest.getHeaders.getHost.getHostName}")
    //@Token
    //@UseSparkStreamingContext
    public Mono<String> hello(ServerWebExchange exchange, Principal principal) {
        return Mono.just(principal.getName());
    }
}
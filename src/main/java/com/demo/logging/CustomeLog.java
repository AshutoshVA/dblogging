package com.demo.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomeLog extends Slf4j{

    @Autowired
    private  static Slf4j log;



    @Override
    public void debug(String format, Object obj, String service) {

        if(service.equals("service"))
        {
            log.debug("test", obj, service);
        }
    }
}

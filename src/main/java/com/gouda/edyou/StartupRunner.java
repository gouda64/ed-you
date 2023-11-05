package com.gouda.edyou;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    @Autowired
    public StartupRunner() {

    }


    @Override
    public void run(String... args) throws Exception {

    }
}

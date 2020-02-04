package com.ncov.stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class NcovStatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NcovStatsApplication.class, args);
    }

}

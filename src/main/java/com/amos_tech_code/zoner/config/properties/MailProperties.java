package com.amos_tech_code.zoner.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zoner.mail")
@Getter
@Setter
public class MailProperties {

    private String from;

}
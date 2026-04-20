package com.ms.credit.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * SplunkConfig sets up the connection details for Splunk HEC (HTTP Event Collector).
 * In a real enterprise setup, log4j2.xml would be configured with a Splunk appender
 * that uses these values. Here we demonstrate the configuration pattern.
 *
 * To fully enable Splunk:
 * 1. Add the Splunk log4j2 appender dependency from splunk.jfrog.io
 * 2. Configure the HEC appender in log4j2.xml with your Splunk instance URL and token
 */
@Configuration
public class SplunkConfig {

    private static final Logger logger = LogManager.getLogger(SplunkConfig.class);

    @Value("${splunk.hec.uri}")
    private String splunkUri;

    @Value("${splunk.hec.token}")
    private String splunkToken;

    @Value("${splunk.hec.index}")
    private String splunkIndex;

    @PostConstruct
    public void init() {
        // Log Splunk config initialization (token hidden for security)
        logger.info("Splunk HEC configured - URI: {}, Index: {}", splunkUri, splunkIndex);
    }

    public String getSplunkUri() {
        return splunkUri;
    }

    public String getSplunkToken() {
        return splunkToken;
    }

    public String getSplunkIndex() {
        return splunkIndex;
    }
}

package com.travelhub.travelhub_api.common.component;

import com.travelhub.travelhub_api.common.configuration.Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {

    private final Properties properties;

    @Scheduled(cron = "${app.cron.reload}")
    public void init() {
        properties.reloadProperties();
    }
}

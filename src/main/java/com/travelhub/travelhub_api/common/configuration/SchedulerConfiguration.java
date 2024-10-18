package com.travelhub.travelhub_api.common.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfiguration implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        final int scheduleThreadPoolSize = 8;

        scheduler.setPoolSize(scheduleThreadPoolSize);
        scheduler.setThreadNamePrefix("schedule-thread-");
        scheduler.initialize();
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskRegistrar.setTaskScheduler(scheduler);
    }
}

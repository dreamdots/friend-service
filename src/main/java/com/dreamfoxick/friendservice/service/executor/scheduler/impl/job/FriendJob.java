package com.dreamfoxick.friendservice.service.executor.scheduler.impl.job;

import com.dreamfoxick.friendservice.service.collector.Collector;
import com.dreamfoxick.friendservice.util.logging.LogUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
public class FriendJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        val collector = (Collector) context.getMergedJobDataMap().get("collector");
        collector.collectFriends().subscribe();
    }
}

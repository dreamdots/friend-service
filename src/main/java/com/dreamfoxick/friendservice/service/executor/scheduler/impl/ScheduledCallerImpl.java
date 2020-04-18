package com.dreamfoxick.friendservice.service.executor.scheduler.impl;

import com.dreamfoxick.friendservice.configuration.YamlPropertySourceFactory;
import com.dreamfoxick.friendservice.service.collector.Collector;
import com.dreamfoxick.friendservice.service.executor.scheduler.ScheduledCaller;
import com.dreamfoxick.friendservice.service.executor.scheduler.impl.job.FriendJob;
import com.dreamfoxick.friendservice.service.executor.scheduler.impl.job.JobGroup;
import com.dreamfoxick.friendservice.service.executor.scheduler.impl.job.UpdateJob;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import com.dreamfoxick.friendservice.util.function.BiVoidRxErrorFunction;
import com.dreamfoxick.friendservice.util.function.ErrorConsumer;
import com.dreamfoxick.friendservice.util.function.VoidRxErrorFunction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;

import static com.dreamfoxick.friendservice.service.executor.scheduler.impl.job.JobGroup.*;
import static java.lang.String.*;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Component
@Profile({"dev", "test"})
@PropertySource(value = "classpath:caller.yml", factory = YamlPropertySourceFactory.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduledCallerImpl implements ScheduledCaller {
    private static final ConcurrentHashMap<JobGroup, ImmutablePair<JobKey, TriggerKey>> keyMap;

    private static final String FRIEND_TRIGGER_KEY;
    private static final String FRIEND_GROUP;
    private static final String FRIEND_JOB_KEY;
    private static final String UPDATE_TRIGGER_KEY;
    private static final String UPDATE_GROUP;
    private static final String UPDATE_JOB_KEY;

    static {
        val fr = FRIEND.getGroup();
        FRIEND_TRIGGER_KEY = format("%s_trigger", fr);
        FRIEND_GROUP = format("%s_group", fr);
        FRIEND_JOB_KEY = format("%s_job", fr);

        val up = UPDATE.getGroup();
        UPDATE_TRIGGER_KEY = format("%s_trigger", up);
        UPDATE_GROUP = format("%s_group", up);
        UPDATE_JOB_KEY = format("%s_job", up);

        keyMap = new ConcurrentHashMap<>();
    }

    private final Collector collector;

    private Scheduler caller;

    @Value("${scheduled.common_friend_cron}")
    private String commonFriendCron;
    @Value("${scheduled.common_update_cron}")
    private String commonUpdateCron;

    @PostConstruct
    @SneakyThrows
    private void initialize() {
        val factory = new StdSchedulerFactory();
        caller = factory.getScheduler();
        _resume();
    }

    private void registerDefault() throws SchedulerException, ParseException {
        // register default friend job
        val context = createContext();
        var cron = new CronExpression(commonFriendCron);
        var job = createJobDetail(FriendJob.class, context, FRIEND_JOB_KEY, FRIEND_GROUP);
        var trigger = createCronTrigger(cron, FRIEND_JOB_KEY, FRIEND_TRIGGER_KEY, FRIEND_GROUP);
        registerKeys(FRIEND, job.getKey(), trigger.getKey());
        caller.scheduleJob(job, trigger);

        // register default update job
        cron = new CronExpression(commonUpdateCron);
        job = createJobDetail(UpdateJob.class, context, UPDATE_JOB_KEY, UPDATE_GROUP);
        trigger = createCronTrigger(cron, UPDATE_JOB_KEY, UPDATE_TRIGGER_KEY, UPDATE_GROUP);
        registerKeys(UPDATE, job.getKey(), trigger.getKey());
        caller.scheduleJob(job, trigger);
    }

    private static CronTrigger createCronTrigger(final CronExpression cron,
                                                 final String jobKey,
                                                 final String triggerKey,
                                                 final String group) {
        return newTrigger()
                .withIdentity(triggerKey, group)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .forJob(jobKey, group)
                .build();
    }

    private static JobDetail createJobDetail(final Class<? extends Job> jobClass,
                                             final JobDataMap context,
                                             final String jobKey,
                                             final String group) {
        return newJob(jobClass)
                .withIdentity(jobKey, group)
                .setJobData(context)
                .build();
    }

    private static void registerKeys(final JobGroup group,
                                     final JobKey jobKey,
                                     final TriggerKey triggerKey) {
        keyMap.put(group, new ImmutablePair<>(jobKey, triggerKey));
    }

    private static void removeAllKeys() {
        keyMap.remove(FRIEND);
        keyMap.remove(UPDATE);
    }

    private static void removeKeys(final JobGroup group) {
        keyMap.remove(group);
    }

    private static ImmutablePair<JobKey, TriggerKey> getKeys(final JobGroup group) {
        return keyMap.get(group);
    }

    @Override
    @LogMethodCall
    public Mono<Boolean> isAlive() {
        return Mono.defer(() -> {
            try {
                return Mono.just(!caller.isInStandbyMode());
            } catch (SchedulerException ex) {
                return Mono.error(ex);
            }
        });
    }

    @Override
    @LogMethodCall
    public Mono<String> report() {
        return Mono.defer(() -> {
            try {
                val info = new StringBuilder();
                return Mono.just(caller.getMetaData().getSummary());
            } catch (SchedulerException ex) {
                return Mono.error(ex);
            }
        });
    }

    @Override
    @LogMethodCall
    public Mono<Void> pause() {
        return Mono.defer(() -> {
            try {
                if (!caller.isInStandbyMode()) {
                    _pause();
                    return Mono.empty();
                } else return Mono.error(new IllegalStateException("Caller already in standby mode"));
            } catch (SchedulerException ex) {
                return Mono.error(ex);
            }
        });
    }

    private void _pause() throws SchedulerException {
        caller.clear();
        removeAllKeys();
        caller.standby();
    }

    @Override
    @LogMethodCall
    public Mono<Void> resume() {
        return Mono.defer(() -> {
            try {
                if (caller.isInStandbyMode()) {
                    _resume();
                    return Mono.empty();
                } else return Mono.error(new IllegalStateException("Caller already started"));
            } catch (SchedulerException | ParseException ex) {
                return Mono.error(ex);
            }
        });
    }

    private void _resume() throws ParseException, SchedulerException {
        registerDefault();
        caller.start();
    }

    @Override
    @LogMethodCall
    public Mono<Void> updateCronExpr(@NonNull final JobGroup group,
                                     @NonNull final String cronExpr) {
        return f(null, this::_update, group, cronExpr);
    }

    @Override
    @LogMethodCall
    public Mono<Void> pauseJob(@NonNull final JobGroup group) {
        return f(this::_pause, null, group, null);
    }

    @Override
    @LogMethodCall
    public Mono<Void> resumeJob(@NonNull final JobGroup group) {
        return f(this::_resume, null, group, null);
    }

    private Mono<Void> _update(final JobGroup group,
                               final String cronExpr) throws ParseException, SchedulerException {
        val keys = getKeys(group);
        val jobKey = keys.getLeft();
        val triggerKey = keys.getRight();
        val cron = new CronExpression(cronExpr);
        val newTrigger = createCronTrigger(cron, jobKey.getName(), triggerKey.getName(), triggerKey.getGroup());
        removeKeys(group);
        registerKeys(group, jobKey, newTrigger.getKey());
        caller.rescheduleJob(triggerKey, newTrigger);
        return Mono.empty();
    }

    private Mono<Void> _pause(final JobGroup group) throws SchedulerException {
        return jobF(caller::pauseJob, group);
    }


    private Mono<Void> _resume(final JobGroup group) throws SchedulerException {
        return jobF(caller::resumeJob, group);
    }

    private Mono<Void> jobF(final ErrorConsumer<JobKey> acc,
                            final JobGroup group) throws SchedulerException {
        val jobKey = getKeys(group).getLeft();
        if (caller.checkExists(jobKey)) {
            acc.apply(jobKey);
            return Mono.empty();
        } else {
            return Mono.error(new IllegalStateException(format("Job by group: %s does not exist", group.getGroup())));
        }
    }

    private Mono<Void> f(final VoidRxErrorFunction<JobGroup> func,
                         final BiVoidRxErrorFunction<JobGroup, String> biFunc,
                         final JobGroup group,
                         final String cronExpr) {
        return Mono.defer(() -> {
            try {
                if (biFunc != null) return biFunc.apply(group, cronExpr);
                else return func.apply(group);
            } catch (SchedulerException | ParseException ex) {
                return Mono.error(ex);
            }
        });
    }

    private JobDataMap createContext() {
        val context = new JobDataMap();
        context.put("collector", collector);
        return context;
    }
}

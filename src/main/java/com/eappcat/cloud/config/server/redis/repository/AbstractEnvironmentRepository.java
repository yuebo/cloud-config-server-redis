package com.eappcat.cloud.config.server.redis.repository;

import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 环境配置读取抽象类
 */
public abstract class AbstractEnvironmentRepository implements EnvironmentRepository, Ordered {

    @Override
    public int getOrder() {
        return 0;
    }
    @Override
    public Environment findOne(String application, String profile, String label) {
        String config = application;
        if (StringUtils.isEmpty(label)) {
            label = "master";
        }
        if (StringUtils.isEmpty(profile)) {
            profile = "default";
        }
        if (!profile.startsWith("default")) {
            profile = "default," + profile;
        }
        String[] profiles = StringUtils.commaDelimitedListToStringArray(profile);
        Environment environment = new Environment(application, profiles, label, null,
                null);
        if (!config.startsWith("application")) {
            config = "application," + config;
        }
        List<String> applications = new ArrayList<String>(new LinkedHashSet<>(
                Arrays.asList(StringUtils.commaDelimitedListToStringArray(config))));
        List<String> envs = new ArrayList<String>(
                new LinkedHashSet<>(Arrays.asList(profiles)));
        Collections.reverse(applications);
        Collections.reverse(envs);
        for (String app : applications) {
            for (String env : envs) {
                addPropertySource(environment, app, env);

            }
        }
        return environment;
    }

    /**
     * 交给子类来读取数据源数据
     * @param environment
     * @param app
     * @param env
     */
    protected abstract void addPropertySource(Environment environment, String app, String env);

}

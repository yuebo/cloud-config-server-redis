package com.eappcat.cloud.config.server.redis.repository;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

@Repository
public class RedisEnvironmentRepository implements EnvironmentRepository, Ordered {

    @Autowired
    private StringRedisTemplate redisTemplate;

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

    private void addPropertySource(Environment environment, String app, String env) {
        String yamlSource = this.redisTemplate.<String,String>opsForHash().get("configServer" , app+"-"+env);
        if(yamlSource==null){
            return;
        }
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        InputStream inputStream=new ByteArrayInputStream(yamlSource.getBytes());
        try {
            yaml.setResources(new InputStreamResource(inputStream));
            Properties next= yaml.getObject();
            if (!next.isEmpty()) {
                environment.add(new PropertySource(app + "-" + env, next));
            }
        }finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

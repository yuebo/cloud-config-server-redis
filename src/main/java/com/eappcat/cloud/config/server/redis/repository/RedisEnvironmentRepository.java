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
public class RedisEnvironmentRepository extends AbstractEnvironmentRepository {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 根据环境从redis读取yml格式，然后解析配置
     * @param environment
     * @param app
     * @param env
     */
    protected void addPropertySource(Environment environment, String app, String env) {
        String yamlSource = this.redisTemplate.<String,String>opsForHash().get("configServer" , app+"-"+env);
        if(yamlSource==null){
            return;
        }
        //将YAML格式转换为properties
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
}

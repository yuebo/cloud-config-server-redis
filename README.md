Cloud Config Server Redis
----------------
使用Redis是实现Spring Cloud Config Server

## Redis 配置格式
使用redis里HASH类型数据
* key: configServer
* hashKey: {application的名称}-{profile的名称}
* value: yml格式的数据

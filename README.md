Cloud Config Server Redis
----------------
使用Redis是实现Spring Cloud Config Server

## Redis 配置格式
使用redis里HASH类型数据
* key: application的名称
* hashKey: profile的名称
* value: yml格式的数据

## CAT 配置说明地址 
```
   文档: https://www.slankka.com/CatCookbook/zh/guide/config/router.html
   官网: https://github.com/dianping/cat/wiki/readme_server
```

### 1、客户端路由配置: /cat/s/config?op=routerConfigUpdate
```
    <?xml version="1.0" encoding="utf-8"?>
    <router-config backup-server="192.168.88.101" backup-server-port="2280">
       <default-server id="192.168.88.101" weight="1.0" port="2280" enable="false"/>
       <default-server id="192.168.88.102" weight="1.0" port="2280" enable="true"/>
       <default-server id="192.168.88.103" weight="1.0" port="2280" enable="true"/>
       <network-policy id="default" title="default" block="false" server-group="default_group">
       </network-policy>
       <server-group id="default_group" title="default-group">
          <group-server id="192.168.88.102"/>
          <group-server id="192.168.88.103"/>
       </server-group>
       <domain id="cat">
          <group id="default">
             <server id="192.168.88.102" port="2280" weight="1.0"/>
             <server id="192.168.88.103" port="2280" weight="1.0"/>
          </group>
       </domain>
    </router-config>
```

### 2、服务端配置
```
<?xml version="1.0" encoding="utf-8"?>
<server-config>
   <server id="default">
      <properties>
         <property name="local-mode" value="false"/>
         <property name="job-machine" value="false"/>
         <property name="send-machine" value="false"/>
         <property name="alarm-machine" value="false"/>
         <property name="hdfs-enabled" value="false"/>
         <property name="remote-servers" value="192.168.88.101:8080,192.168.88.102:8080,192.168.88.103:8080"/>
      </properties>
      <storage local-base-dir="/data/appdatas/cat/bucket/" max-hdfs-storage-time="60" local-report-storage-time="5" local-logivew-storage-time="4" har-mode="true" upload-thread="5">
         <hdfs id="logview" max-size="128M" server-uri="hdfs://10.1.77.86/" base-dir="user/cat/logview"/>
         <hdfs id="dump" max-size="128M" server-uri="hdfs://10.1.77.86/" base-dir="user/cat/dump"/>
         <hdfs id="remote" max-size="128M" server-uri="hdfs://10.1.77.86/" base-dir="user/cat/remote"/>
      </storage>
      <consumer>
         <long-config default-url-threshold="1000" default-sql-threshold="100" default-service-threshold="50">
            <domain name="cat" url-threshold="500" sql-threshold="500"/>
            <domain name="OpenPlatformWeb" url-threshold="100" sql-threshold="500"/>
         </long-config>
      </consumer>
   </server>
   <server id="192.168.88.103">
      <properties>
         <property name="job-machine" value="true"/>
         <property name="alarm-machine" value="true"/>
         <property name="send-machine" value="true"/>
      </properties>
   </server>
</server-config>
```




### 注意点
```
1、经测试: 在页面更新:客户端路由配置和服务端配置，重启其它节点才能看到变化
2、重启不影响数据可用性
  单机模式部署可略过此步骤，线上环境使用建议配置。
  请在tomcat重启之前调用当前tomcat的存储数据的链接 http://${ip}:8080/cat/r/home?op=checkpoint，重启之后数据会恢复。【注意重启时间在每小时的整点10-55分钟之间】
  线上部署时候，建议把此链接调用存放于tomcat的stop脚本中，这样不需要每次手工调用
```
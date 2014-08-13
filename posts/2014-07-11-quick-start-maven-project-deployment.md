% Maven项目发布到公司内部Repository(Nexus) #阶段性反刍#
% FuqiangWang

# 配置结构关系

## 项目pom.xml配置

第一， 先配置发布到哪个repository相关信息:


~~~~~~~ {.xml}
    <distributionManagement>
        <repository>
            <id>deployment</id>
            <name>internal repository for releases</name>
            <url>http://192.168.1.248:9111/nexus/content/repositories/releases</url>
        </repository>
        <snapshotRepository>
            <id>deployment</id>
            <name>internal repository for snapshots</name>
            <url>http://192.168.1.248:9111/nexus/content/repositories/snapshots</url>
        </snapshotRepository>
    </distributionManagement>
~~~~~~~



第二， 配置deploy插件：

~~~~~~~ {.xml}
        <plugins>
            <plugin>
                <artifactId>maven-deploy-plugin</artifactId>
            </plugin>
        </plugins>
~~~~~~~



## settings.xml配置
Nexus通常会设置认证， 只允许指定的credentials可以发布artifacts到nexus， 而这些认证信息就配置在settings.xml中：


~~~~~~~ {.xml}
    <servers>
        <server>
            <id>deployment</id>
            <username>deployment</username>
            <password>${password}</password>
        </server>
    </servers>
~~~~~~~


> 注意： server的id值需要跟pom.xml中每一个repository的id值相对应！！！ （因为我们是为这些distributionManagement的repository提供认证信息）

# 执行发布/部署

在当前项目目录下执行`mvn deploy`坐等发布成功！




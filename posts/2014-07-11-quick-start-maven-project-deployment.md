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

顺便把这些plugin一起配置，这样可以jar， source jar， javadoc jar一起发布：

~~~~~~~ {.xml}
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${java_source_version}</source>
                    <target>${java_target_version}</target>
                    <encoding>${file_encoding}</encoding>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <executions>
                    <execution>
                        <id>attach-sources</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <configuration>
                    <charset>${file_encoding}</charset>
                    <encoding>${file_encoding}</encoding>
                </configuration>
                <executions>
                    <execution>
                        <id>attach-javadocs</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
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











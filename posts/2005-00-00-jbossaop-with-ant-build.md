% 有关JbossAop编译部署的ant脚本！
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

近来看了JbossAop的相关资料，也做过几个demo，对于其原理也稍有了解，现将编译用的ant脚本罗列如下，以备后查。呵呵，十分简单，但是本人记性不好，所以，为了以后copy好用。

~~~~~~~ {.xml}
<?xml version="1.0" encoding="UTF-8"?>

<project default="simple.run" name="JBoss/AOP" basedir=".">
 
 <!--
  |Properties for Project
 -->
 <property name="src.dir" value="src"/>
 <property name="lib.dir" value="lib"/>
 <property name="build.dir" value="build"/>
 <property name="build.classes.dir" value="{build.dir}/classes"/>
 <property name="build.jar.dir" value="{build.dir}/jar"></property>
 <property name="jdk.lib.dir" value=""/>
 <property name="dist.dir" value="dist"/>
 <property name="aop.config.path" value="jboss-aop.xml"/>
 
    <path id="jbossaop.classpath">
         <fileset dir="{lib.dir}">
            <include name="**/*.jar"/>
         </fileset>
 </path>
 
    <path id="runtime.classpath">
       <path refid="jbossaop.classpath"/>
       <pathelement location="{build.classes.dir}"/>
    </path>
 
 <target name="init">
  <mkdir dir="{build.dir}"/>
  <mkdir dir="{build.classes.dir}"/>
  <mkdir dir="{build.jar.dir}"/>
  <mkdir dir="{dist.dir}"/>
  
     <taskdef name="annotationc" classname="org.jboss.aop.ant.AnnotationC" classpathref="jbossaop.classpath"/>
     <taskdef name="aopc" classname="org.jboss.aop.ant.AopC" classpathref="jbossaop.classpath"/>
 </target>

 <target name="compile" depends="init" description="Compile the Java File to Class for Later usage!">
  <javac srcdir="{src.dir}" destdir="{build.classes.dir}" debug="on">
   <classpath refid="jbossaop.classpath"></classpath>
  </javac>
 </target>
 
 <target name="annotationc" depends="compile" description="Compile for Annotation in JDK1.4! ">
       <annotationc compilerclasspathref="runtime.classpath" classpathref="runtime.classpath" bytecode="true">
          <src path="{build.classes.dir}"/>
       </annotationc>
 </target>
 
 <target name="aopc" depends="compile">
  <aopc compilerclasspathref="runtime.classpath" classpathref="runtime.classpath" verbose="true">
   <src path="{build.classes.dir}"></src>
   <aoppath>
    <pathelement path="{aop.config.path}"/>
   </aoppath>
  </aopc>
 </target>
 
 <target name="simple.run" depends="aopc" description="Run Simple Example!">
       <java fork="yes" failOnError="true" className="org.dwstudio.aop.simple.business.AopDriver">
          <sysproperty key="jboss.aop.path" value="{aop.config.path}"/>
          <classpath refid="runtime.classpath"/>
       </java>
 </target>
 
 <target name="ui.run">
       <java fork="yes" failOnError="true" className="org.dwstudio.aop.simple.business.AopDriver">
          <sysproperty key="jboss.aop.path" value="{aop.config.path}"/>
          <classpath refid="runtime.classpath"/>
       </java>
 </target>
 
 <target name="release" depends="aopc" description="Release the Aop Project!">
  <jar destfile="{build.jar.dir}/dwaop.jar">
   <fileset dir="{build.classes.dir}">
   </fileset>
  </jar>
  <jar destfile="{dist.dir}/final.jar">
   <manifest>
    <attribute name="Created-By" value="Darren.Wang"/>
    <attribute name="Class-Path" value="dwaop.jar concurrent.jar javassist.jar jboss-aop.jar jboss-aspect-library.jar jboss-common.jar qdox.jar trove.jar"/>
    <attribute name="Main-Class" value="org.dwstudio.aop.simple.business.AopDriver"/>
   </manifest>
   <metainf dir="." includes="{aop.config.path}"></metainf>
   <fileset dir="{lib.dir}">
    <include name="**/*.jar"/>
   </fileset>
   <fileset dir="{build.jar.dir}">
   </fileset>
  </jar>
 </target>
 
 <target name="clean" description="Clean the Workplace for a new building!">
  <delete dir="{build.dir}"></delete>
  <delete dir="{dist.dir}"></delete>
 </target>

</project>
~~~~~~~

对于JbossAOP来说，因为其weaving过程是在runtime进行的，所以需要在运行的时候为其classloader指定jboss-aop.xml文件。

其实，jbossaop虽然说是随jboss as发布的，但是，也可作为standalone使用，只是，与其他不同的java文件编译所不同的是，在javac之后，我们还需要对于javac生成后的class进行aopc编译，为jbossaop的classloader做准备，其中annotationc是可选的（对于jdk5来说，因为已经提供了annotation的支持，所以无需另外处理，因为其annotation支持runtime类型，所以，可以在runtime时期直接取得并处理），只是对于jdk1.4,并且需要使用annotation的场合，才会需要该task对jbossaop规定的1.4形势的annotation进行预处理。

因为现在机器上使用的是1.4的jdk，所以，以上ant脚本是针对1.4的，如果像我笔记本上那样使用jdk5.0的环境，在运行前，根本不需要执行annotationc和aopc这两个任务。具体情况可以参照jbossaop的reference。

另外，对于"release"这个target，其实打包成jar是没有问题的，但是，self-execute确实不可能的，因为这个是由jar文件的classloader类装载逻辑所限制的，对于这种问题的产生和处理方法，可以参照一下两个URL：

1. http://forum.java.sun.com/thread.jspa?forumID=22&threadID=161143
2. http://www-900.ibm.com/developerWorks/cn/java/j-onejar/index.shtml

他们对于这个问题都有详细的讲解。

其他有关JbossAop的更多信息将会在不久的将来陆续给出，呵呵，OK，let''s stop here today!:em510:
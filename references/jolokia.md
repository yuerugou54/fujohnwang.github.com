% JMX MBeans Via Jolokia Over HTTP
% FuqiangWang


----------------------------------------------


http://localhost:8778/jolokia/exec/com.sun.management:type=DiagnosticCommand/vmCommandLine

http://localhost:8778/jolokia/exec/com.sun.management:type=DiagnosticCommand/vmSystemProperties

http://localhost:8778/jolokia/read/java.lang:type=Memory/HeapMemoryUsage

http://localhost:8778/jolokia/read/java.lang:type=OperatingSystem/*

http://localhost:8778/jolokia/read/java.lang:type=Runtime/*

http://localhost:8778/jolokia/read/java.lang:type=GarbageCollector,name=PS%20MarkSweep/*   --- (Old GC)

http://localhost:8778/jolokia/read/java.lang:type=GarbageCollector,name=PS%20Scavenge/*    --- (Young GC)


----------------------------------------------


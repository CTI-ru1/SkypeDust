cd /home/carnage/Programming/SkypeDust/SkypeDust/SkypeDustm; 
JAVA_HOME=/usr/java/latest 
/home/carnage/netbeans-7.1/java/maven/bin/mvn "-Dexec.args=-classpath %classpath com.skypedust.SkypeDustMain start" -Dexec.executable=/usr/java/latest/bin/java -Dexec.classpathScope=runtime process-classes org.codehaus.mojo:exec-maven-plugin:1.2:exec


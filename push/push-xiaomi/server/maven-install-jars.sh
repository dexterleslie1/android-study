#!/bin/bash

mvn install:install-file -Dpackaging=jar -Dfile=./libs/commons-codec-1.12.jar -DgroupId=com.xiaomi.xmpush -DartifactId=commons-codec -Dversion=1.12
mvn install:install-file -Dpackaging=jar -Dfile=./libs/conscrypt-openjdk-uber-2.1.0.jar -DgroupId=com.xiaomi.xmpush -DartifactId=conscrypt-openjdk-uber -Dversion=2.1.0
mvn install:install-file -Dpackaging=jar -Dfile=./libs/json-simple-1.1.jar -DgroupId=com.xiaomi.xmpush -DartifactId=json-simple -Dversion=1.1
mvn install:install-file -Dpackaging=jar -Dfile=./libs/MiPush_SDK_Server_Http2_1.0.9.jar -DgroupId=com.xiaomi.xmpush -DartifactId=MiPush_SDK_Server_Http2 -Dversion=1.0.9
mvn install:install-file -Dpackaging=jar -Dfile=./libs/okhttp-3.14.2.jar -DgroupId=com.xiaomi.xmpush -DartifactId=okhttp -Dversion=3.14.2
mvn install:install-file -Dpackaging=jar -Dfile=./libs/okio-1.17.2.jar -DgroupId=com.xiaomi.xmpush -DartifactId=okio -Dversion=1.17.2
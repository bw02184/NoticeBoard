### build statge ###
FROM openjdk:11 AS builder

# set args
ARG WORKSPACE=/src/sws
ARG BUILD_TARGET=${WORKSPACE}/build/libs
WORKDIR ${WORKSPACE}

# copy & build
COPY . .
#window os라서 개행문자 변경해 주는 dos2unix설치 후 gradlew 변경
RUN apt-get update \
    && apt-get install dos2unix \
    && dos2unix ./gradlew

RUN chmod +x ./gradlew
RUN ./gradlew clean booJar

# unpack jar
WORKDIR ${BUILD_TARGET}
RUN jar -xf *.jar


### image stage ###
FROM openjdk:11

# set args
ARG WORKSPACE=/src/sws
ARG BUILD_TARGET=${WORKSPACE}/build/libs
ARG DEPLOY_PATH=${WORKSPACE}/deploy

# copy
COPY --from=builder ${BUILD_TARGET}/org ${DEPLOY_PATH}/org
COPY --from=builder ${BUILD_TARGET}/BOOT-INF/lib ${DEPLOY_PATH}/BOOT-INF/lib
COPY --from=builder ${BUILD_TARGET}/META-INF ${DEPLOY_PATH}/META-INF
COPY --from=builder ${BUILD_TARGET}/BOOT-INF/classes ${DEPLOY_PATH}/BOOT-INF/classes

WORKDIR ${DEPLOY_PATH}

EXPOSE 8080/tcp
ENTRYPOINT ["java","org.springframework.boot.loader.JarLauncher"]
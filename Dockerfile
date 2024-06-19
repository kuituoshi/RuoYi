FROM fastcn-registry-changsha42.crs.ctyun.cn/hnkz/springboot:8-openj9

RUN set -ex \
	&& chown -R java:java /workdir \
	&& apk add --no-cache ttf-dejavu

COPY ruoyi-admin/target/ruoyi-admin.jar /workdir/boot.jar

ENV SPRINGBOOT_ACTIVE_PROFILE=prod

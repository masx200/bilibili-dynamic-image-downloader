# bilibili-dynamic-image-downloader

#### 介绍

bilibili-dynamic-image-downloader

#### 软件架构

哔哩哔哩动态图片下载器

#### 安装教程

```shell
gradle buildFatJar
```

#### 使用说明

```shell
java   -jar "bilibili-dynamic-image-downloader-all.jar" "--cookie=*****=****" "--host_uid=********" -o "*****"
```

## Running

Execute this command to run this sample:

```bash
./gradlew run --args="--cookie=*****=**** --host_uid=******** -o *****"
```

```
bilibili-dynamic-image-downloader
usage: bilibili-dynamic-image-downloader [-h] [-c COOKIE] [-u HOST_UID]
                                         [-o OFFSET_DYNAMIC_ID]

optional arguments:
  -h, --help                              show this help message and exit

  -c COOKIE, --cookie COOKIE              cookie

  -u HOST_UID, --host_uid HOST_UID        host_uid

  -o OFFSET_DYNAMIC_ID,                   offset_dynamic_id
  --offset_dynamic_id OFFSET_DYNAMIC_ID
```

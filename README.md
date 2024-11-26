# bilibili-dynamic-image-downloader

#### 介绍

bilibili-dynamic-image-downloader

#### 软件架构

哔哩哔哩动态图片下载器

#### 安装教程

```shell
gradle shadowJar
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
usage: bilibili-dynamic-image-downloader [-h] -c COOKIE -u HOST_UID
                                         [-o OFFSET_DYNAMIC_ID]
                                         [-e ENDWITH_DYNAMIC_ID]

required arguments:
  -c COOKIE, --cookie COOKIE                cookie

  -u HOST_UID, --host_uid HOST_UID          host_uid


optional arguments:
  -h, --help                                show this help message and exit

  -o OFFSET_DYNAMIC_ID,                     offset_dynamic_id
  --offset_dynamic_id OFFSET_DYNAMIC_ID

  -e ENDWITH_DYNAMIC_ID,                    endwith_dynamic_id
  --endwith_dynamic_id ENDWITH_DYNAMIC_ID
```

参数 COOKIE 是bilibili账号的cookie

参数 HOST_UID是指定要下载的up主，即bilibili账号的uid

参数 offset_dynamic_id是指定从此id之后的动态，不包含此id的动态

参数 endwith_dynamic_id是指定到此id之前的动态，不包含此id的动态

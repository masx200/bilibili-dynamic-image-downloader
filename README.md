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

[//]: # (```shell)
[//]: # (java   -jar "bilibili-dynamic-image-downloader-all.jar" "--cookie=*****=****" "--host_uid=********" -o "*****")
[//]: # (```)
[//]: # ()
[//]: # (```shell)
[//]: # (java   -jar "bilibili-dynamic-image-downloader-all.jar"    -c "*****=*****"  -u "*********" -d "./file_dynamic_ids.txt" -i "./file_dynamic_images.txt")
[//]: # (```)

```shell
java   -jar "bilibili-dynamic-image-downloader-all.jar" "--cookie=*****=****" "--host_uid=********" -s "*****"  -o "*****" -e "*****"
```

## Running

Execute this command to run this sample:

```bash
./gradlew run --args="--cookie=*****=**** --host_uid=******** -o *****   -s *****"
```

# Usage

```
bilibili-dynamic-image-downloader
usage: bilibili-dynamic-image-downloader [-h] -c COOKIE -u HOST_UID
                                         [-o OFFSET_DYNAMIC_ID]
                                         [-e endwith_dynamic_id]
                                         -s DOWNLOAD_STATE_FILE

required arguments:
  -c COOKIE, --cookie COOKIE                  cookie

  -u HOST_UID, --host_uid HOST_UID            host_uid

  -s DOWNLOAD_STATE_FILE,                     download_state_file
  --download_state_file DOWNLOAD_STATE_FILE


optional arguments:
  -h, --help                                  show this help message and exit

  -o OFFSET_DYNAMIC_ID,                       offset_dynamic_id
  --offset_dynamic_id OFFSET_DYNAMIC_ID

  -e endwith_dynamic_id,                      endwith_dynamic_id
  --endwith_dynamic_id endwith_dynamic_id
```

[//]: # (         [-d FILE_DYNAMIC_IDS])
[//]: # (                                         [-i FILE_DYNAMIC_IMAGES])
[//]: # (  -d FILE_DYNAMIC_IDS,                        file_dynamic_ids)
[//]: # (  --file_dynamic_ids FILE_DYNAMIC_IDS)
[//]: # ()
[//]: # (  -i FILE_DYNAMIC_IMAGES,                     file_dynamic_images)
[//]: # (  --file_dynamic_images FILE_DYNAMIC_IMAGES)

参数 `COOKIE` 是bilibili账号的cookie

参数 `HOST_UID`是指定要下载的up主，即bilibili账号的uid

参数 `offset_dynamic_id`是指定从此id之后的动态，不包含此id的动态

参数 `endwith_dynamic_id`是指定到此id之前的动态，不包含此id的动态

[//]: # (参数 `file_dynamic_ids`是指定一个文件，文件中每行一个动态id，在文件中写入动态id)
[//]: # ()
[//]: # (参数 `file_dynamic_images`是指定一个文件，文件中每行一个动态图片，在文件中写入动态图片地址)

参数 `download_state_file`
是指定一个文件,使用sqlite格式，保存了下载进度的成功和失败的记录

## 状态保存功能

由于可能因为网络卡顿,或者被服务器反爬限速, 导致程序中断。

程序支持状态保存功能，可以在程序中断后恢复下载进度。状态信息会保存到
`DOWNLOAD_STATE_FILE`参数指定的文件，例如为 `download_state.sqlite.db` 文件中。

### 使用方法

1. 运行程序时，程序会自动读取之前保存的状态文件。
2. 下载过程中，程序会定期保存当前的状态到文件中。

## 鸣谢

https://github.com/artbits/sqlite-java

https://github.com/JetBrains/Exposed

https://github.com/lengpucheng/BilibiliClient

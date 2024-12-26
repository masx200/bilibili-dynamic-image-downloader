package com.github.masx200.bilibili_dynamic_image_downloader

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody

/**
 * The entry point of the program.
 * Parses command line arguments and processes Bilibili dynamic image downloads.
 *
 * @param args Command line arguments, containing parameters for program operation.
 */
fun main(args: Array<String>) {

    /**
     * 打印[MyArgs]类中的参数信息
     * 此函数接收一个[MyArgs]对象作为参数，从中提取并打印出各个字段的值
     * 主要用于调试或日志记录，以帮助开发者理解传递给函数的参数值
     *
     * @param options 包含参数的[MyArgs]对象，用于提取并打印参数信息
     */
    fun printmyargs(options: MyArgs) {

        println(options.toString())

    }
    println("bilibili-dynamic-image-downloader")
    println(args.contentToString())
    mainBody("bilibili-dynamic-image-downloader") {

        // Creates a Netty server
        ArgParser(args).parseInto(::MyArgs).run {
            printmyargs(this)
            val iteritems = getDynamicSequence(this)

            val (idsWriter, imagesWriter) = createWriteStreamsIfNotEmpty(file_dynamic_ids, file_dynamic_images)

            if (idsWriter != null && imagesWriter != null) {
                idsWriter.use { idsWriter ->
                    imagesWriter.use { imagesWriter ->
                        processDynamicItems(iteritems, idsWriter, imagesWriter)
                    }
                }
            } else {
                processDynamicItems(iteritems)
            }

        }
    }

}




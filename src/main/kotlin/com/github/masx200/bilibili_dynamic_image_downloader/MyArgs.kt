package com.github.masx200.bilibili_dynamic_image_downloader

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

/**
 * Class for parsing command line arguments.
 * This class uses the ArgParser library to define and store command line arguments.
 * The purpose of this class is to provide easy access to command line arguments, converting them into properties for use.
 *
 * @param parser An instance of ArgParser used to parse command line arguments.
 */
data class MyArgs(var parser: ArgParser) {
    val cookie by parser.storing(
        "-c", "--cookie",
        help = "cookie"
    ).default("")
    val cookie_file by parser.storing(
        "--cookie-file",
        help = "cookie-file"
    ).default("")
    val host_uid by parser.storing(
        "-u", "--host_uid",
        help = "host_uid"
    )
    val offset_dynamic_id by parser.storing(
        "-o", "--offset_dynamic_id",
        help = "offset_dynamic_id"
    ).default("")

    val endwith_dynamic_id by parser.storing(
        "-e", "--endwith_dynamic_id",
        help = "endwith_dynamic_id"
    ).default("")

    //    val file_dynamic_ids by parser.storing(
//        "-d", "--file_dynamic_ids", help = "file_dynamic_ids"
//    ).default("")
//    val file_dynamic_images by parser.storing(
//        "-i", "--file_dynamic_images", help = "file_dynamic_images"
//    ).default("")
    val download_state_file: String by parser.storing(
        "-s", "--download_state_file", help = "download_state_file"
    )//.default("")
    val force_recreate: Boolean by parser.flagging("--force_recreate", help = "force_recreate")
    

}



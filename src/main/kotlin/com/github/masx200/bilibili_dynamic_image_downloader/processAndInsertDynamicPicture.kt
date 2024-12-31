package com.github.masx200.bilibili_dynamic_image_downloader

import com.github.masx200.biliClient.model.dynamic.Dynamic
import com.github.masx200.biliClient.model.dynamic.DynamicData
import com.github.masx200.biliClient.model.dynamic.Picture
import com.github.masx200.bilibili_dynamic_image_downloader.entity.DynamicPictures

/**
 * 处理动态图片信息并插入到数据库。
 *
 * 此函数接收一个图片对象、动态项、动态数据、主机用户ID、一个回调列表以及一个数据库表访问对象。
 * 它提取图片的URL，创建一个新的 [com.github.masx200.bilibili_dynamic_image_downloader.entity.DynamicPictures] 实例，填充必要的字段，
 * 并在确保所有必要字段不为空的情况下，将插入操作作为回调添加到 [datatoinsertcallbacks] 列表中。
 *
 * @param picture 图片对象，可以为空，但必须包含 img_src 属性。
 * @param item 动态项，包含描述信息。
 * @param data 动态数据，可以为空，但必须包含 dynamic_id_str。
 * @param host_uid 主机用户ID，用于关联图片记录。
 * @param datatoinsertcallbacks 回调列表，用于稍后批量执行插入操作。
 * @param dynamicPicturesTable 数据库表访问对象，用于执行插入操作。
 */
fun processAndInsertDynamicPicture(
    picture: Picture?,
    item: Dynamic,
    data: DynamicData?,
    host_uid: String,
    datatoinsertcallbacks: MutableList<() -> Unit>,
    dynamicPicturesTable: DataBaseTableDao<DynamicPictures>
) {
    val str = picture!!.img_src

    str?.let { url ->


        println(url)


        var dynamicPictures = DynamicPictures {
            it.dynamicOriginId = item.desc?.orig_dy_id_str?.toLong()
//            it.dynamicOriginType = item.desc?.orig_type?.toLong()
            it.dynamicId = data!!.dynamic_id_str?.toLong()
            it.pictureSrc = url.toString()
            it.userId = host_uid
        }
        println(dynamicPictures)
        assert(
            dynamicPictures.dynamicId != null && dynamicPictures.pictureSrc != null && dynamicPictures.userId != null
        )
        datatoinsertcallbacks.add {
            println(
                dynamicPicturesTable.insert(dynamicPictures)
            )
        }
    }
}
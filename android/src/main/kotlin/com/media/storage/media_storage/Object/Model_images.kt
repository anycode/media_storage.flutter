package com.media.storage.media_storage.Object

import android.net.Uri


class Model_images {

    var str_folder: String? = null
    var al_imagepath: ArrayList<Image>? = null

    @JvmName("setStr_folder1")
    fun setStr_folder(str_folder: String) {
        this.str_folder = str_folder
        Debug.e("set_mages===>folder__", "" + str_folder)
    }

    @JvmName("setAl_imagepath1")
    fun setAl_imagepath(al_imagepath: ArrayList<Image>) {
        this.al_imagepath = al_imagepath
        Debug.e("set_mages===>", "" + al_imagepath.size)
    }

    class Image {
        var imgPath: String? = null
        var thumbImgPath: String? = null
        var name: String? = null
        var height: String? = null
        var width: String? = null
        var date: Long = 0
        var video_duration: Long = 0
        var size: Long = 0
        var imgType = 0
        var imgIndex = 0
        var isFav = false
        var id: Long = 0
        var contentUri: Uri? = null

        override fun toString(): String {
            return "Image{" +
                    ", name='" + name + '\'' +
                    ", img_index=" + imgIndex +
                    ", isFav=" + isFav +
                    '}'
        }
    }

    override fun toString(): String {
        return "Model_images{" +
                "str_folder='" + str_folder + '\'' +
                '}'
    }
}

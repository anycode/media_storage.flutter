package com.media.storage.media_storage.Object

import android.net.Uri

class AllData {
//    var str_folder: String? = null
    var mediaId: Long? = null
    var displayName: String? = null
    var fileUri: String? =null
    var filepath: String? = null
    var dateCreate: Long? = null
    var dateModified: Long? = null
    var media_type: Int? = null
    var mime_type: String? = null
    var height: String? = null
    var width: String? = null
    var video_duration: Long = 0
    var size: String? = null
    override fun toString(): String {
        return "AllData(mediaId=$mediaId, displayName=$displayName, fileUri=$fileUri, filepath=$filepath, dateCreate=$dateCreate, dateModified=$dateModified, media_type=$media_type, mime_type=$mime_type, height=$height, width=$width, video_duration=$video_duration, size=$size)"
    }


}
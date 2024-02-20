package com.media.storage.media_storage


import Debug
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.app.RecoverableSecurityException
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.media.storage.media_storage.Object.AllData
import com.media.storage.media_storage.Object.Model_images
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


private const val DELETE_PERMISSION_REQUEST = 0x1033
private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045


/** MethodCallHandlerImpl */
class MethodCallHandlerImpl(private val context: Context) :
	MethodChannel.MethodCallHandler,
	MediaStorageTaskPluginChannel,
	PluginRegistry.ActivityResultListener {
	private lateinit var channel: MethodChannel

	private var isPermission : Boolean = false
	private var activity: Activity? = null
	var isDeleted: Boolean = false


	var al_images: ArrayList<Model_images> = ArrayList<Model_images>()
	var al_sub_images: ArrayList<Model_images.Image> = ArrayList<Model_images.Image>()
	var al_filepath: ArrayList<String> = ArrayList<String>()

	val allDatamodel: AllData = AllData()
	var mresult: MethodChannel.Result? = null

	var allDataList: ArrayList<AllData> = ArrayList<AllData>()
	private var TAG: String = "MethodCallHandlerImpl"


	 @RequiresApi(Build.VERSION_CODES.Q)
	 override  fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
		val callMethod = call.method
		val callArguments = call.arguments
//		Log.e(TAG, "onMethodCall: " + callMethod )

		when (callMethod) {
			"getRequestStoragePermission" ->{

					if(!haveStoragePermission()){
//					Log.e(TAG, "onMethodCall: " + "permission_not_granted" )
					val permissions = arrayOf(
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
					)
						activity?.let { ActivityCompat.requestPermissions(it, permissions, READ_EXTERNAL_STORAGE_REQUEST) }

					}
				result.success(haveStoragePermission())

			}
			"getExternalStorageDirectories" ->
				result.success(getExternalStorageDirectories())
			"getExternalStoragePublicDirectory" ->
				result.success(getExternalStoragePublicDirectory(call.argument<String>("type")))
			"getMediaStoreData" ->{

				result.success(getMediaStoreData(call.argument<String>("path")))
			}
			"deleteFile" ->{
				//mresult =result
				deleteFile(call.argument<String>("deletepath"),result)
			}
			/*"deleteDir" ->{
				mresult =result
				result.success(deleteDir(call.argument<String>("deleteDirpath")?.let { File(it) }))

			}*/
			"createDirectory" ->{
				//mresult =result
				createDirectory(call.argument<String>("createfolderpath"),call.argument<String>("foldername"),result)
			}


			else -> result.notImplemented()
		}

	}

	fun createDirectory(folderPath: String?,foldername: String?, result: MethodChannel.Result){

		val myDirectory = foldername?.let { File(folderPath, it) }

		if(!myDirectory!!.exists()) {
			myDirectory.mkdirs()
		}
		result.success(myDirectory.absolutePath)
	}


	fun deleteDir(dir: File?): Boolean {

		Log.e(TAG, "deleteDir: " + dir!!.absolutePath )
		if (dir.isDirectory) {
			val children = dir.list()
			if (children != null) {
				for (i in children.indices) {
					val temp = File(dir, children[i])
					if (temp.isDirectory) {
						Log.e("DeleteRecursive", "Recursive Call" + temp.path)
						deleteDir(temp)
					} else {
						Log.e("DeleteRecursive", "Delete File" + temp.parent)
						try {
							val b = context.deleteFile(temp.absolutePath)
							if (!b) {
								Log.e("DeleteRecursive", "DELETE FAIL")
							}
						} catch (e: Exception) {
							Log.e(TAG, "deleteDir_exception: " +e.message )
						}
					}
				}
			}
		}

		return dir.delete()
	}




	 @RequiresApi(Build.VERSION_CODES.Q)
	 @OptIn(DelicateCoroutinesApi::class)
	 fun deleteFile(image: String?, result: MethodChannel.Result)   {

//		 getFilePathFromContentUri("",context.contentResolver)

		 var extRootPaths_: ArrayList<AllData> = ArrayList()
		 extRootPaths_.isEmpty()
		 try {
			 extRootPaths_ =  getExternalMediaList();

			
			 for (i in extRootPaths_.indices) {
				 if(image.equals(extRootPaths_[i].filepath)){

					 allDatamodel.fileUri = extRootPaths_[i].fileUri
					 allDatamodel.mediaId = extRootPaths_[i].mediaId
					 allDatamodel.filepath = extRootPaths_[i].filepath
				 }
			 }

			 Log.e(TAG, "deleteFile_uri "+ allDatamodel.fileUri   )
			 val collection = ArrayList<String>()
			 Uri.parse(allDatamodel.fileUri)?.let { collection.add(it.toString()) }

			 GlobalScope.launch {
				 if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {

					 deleteMediaR(activity!!,collection)

				 }else if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){

					 performDeleteImage(allDatamodel,result)
					 Log.e(TAG, "getdeleteMedia: " +isDeleted )
				 }
			 }

		 } catch (e: Exception) {
			 Log.e(TAG, "getMediaStoreData_0909 " + e.message )
		 }
		 


	}


	private fun deleteMediaR(activity: Activity, uris: ArrayList<String>) {
		try {
			val contentResolver = context.contentResolver
			val collection: ArrayList<Uri> = ArrayList()
			collection.addAll(uris.map { uri -> Uri.parse(uri) })
			val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				MediaStore.createDeleteRequest(contentResolver, collection)
			} else {
				TODO("VERSION.SDK_INT < R")
			}
			Log.e(TAG, "deleteMediaR: " +pendingIntent )

			activity.startIntentSenderForResult(pendingIntent.intentSender, DELETE_PERMISSION_REQUEST, null, 0, 0, 0, null)
		} catch (e: Exception) {
			Log.e(TAG, "deleteMediaR_exception: " +e.message )
		}


	}

	private  fun performDeleteImage(allData:AllData,result: MethodChannel.Result) {

			try {

				/**
				 * In [Build.VERSION_CODES.Q] and above, it isn't possible to modify
				 * or delete items in MediaStore directly, and explicit permission
				 * must usually be obtained to do this.
				 *
				 * The way it works is the OS will throw a [RecoverableSecurityException],
				 * which we can catch here. Inside there's an [IntentSender] which the
				 * activity can use to prompt the user to grant permission to the item
				 * so it can be either updated or deleted.
				 */
				Uri.parse(allData.fileUri)?.let {
					context.contentResolver.delete(
						it,
						"${MediaStore.Images.Media._ID} = ?",
						arrayOf(allData.mediaId.toString())
					)
				}

				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ){
					if(context.contentResolver.delete(
							Uri.parse(allData.fileUri)!!,
						"${MediaStore.Images.Media._ID} = ?",
						arrayOf(allData.mediaId.toString())
					) == 0){
						result.success(true)
				}else{
						result.success(false)
					}
				}else{
					result.success(true)
				}


			} catch (securityException: SecurityException) {
				Log.e(TAG, "performDeleteImage: " +securityException.message )
				if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

					val recoverableSecurityException =
						securityException as? RecoverableSecurityException
							?: throw securityException

					// Signal to the Activity that it needs to request permission and
					// try the delete again if it succeeds.

					val pendingIntent: PendingIntent = recoverableSecurityException.userAction.actionIntent
					activity!!.startIntentSenderForResult(pendingIntent.intentSender,
						DELETE_PERMISSION_REQUEST, null, 0, 0, 0, null)
				} else {
					throw securityException
				}


		}

	}


	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
		if (resultCode == Activity.RESULT_OK && requestCode == DELETE_PERMISSION_REQUEST) {
			mresult?.success(true)
			mresult = null

			Log.e(TAG, "onActivityResult: " )
		}else{
			mresult?.success(false)
			mresult = null
		}
		return isDeleted
	}

	@SuppressLint("Range")
	private fun getFilePathFromContentUri(
		selectedVideoUri: String,
		contentResolver: ContentResolver,
	): ArrayList<String> {
		al_images.clear()
		val cursor: Cursor?
		var boolean_folder = false
		try {
			val projection = arrayOf(
				MediaStore.Files.FileColumns._ID,
				MediaStore.Files.FileColumns.DATA,
				MediaStore.Files.FileColumns.DATE_ADDED,
				MediaStore.Files.FileColumns.MEDIA_TYPE,
				MediaStore.Files.FileColumns.MIME_TYPE,
				MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
				MediaStore.Video.VideoColumns.DURATION,
				MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
				MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME,
				MediaStore.Files.FileColumns.TITLE,
				MediaStore.Images.Thumbnails.DATA,
				MediaStore.Video.Thumbnails.DATA,
				MediaColumns.SIZE,
				MediaColumns.WIDTH,
				MediaColumns.HEIGHT,
				MediaColumns.DISPLAY_NAME,
				MediaStore.Downloads._ID,
				MediaStore.Downloads.DISPLAY_NAME
//				MediaStore.Files.FileColumns._ID,
//				MediaStore.Files.FileColumns.DATA,
//				MediaStore.Files.FileColumns.DATE_ADDED,
//				MediaStore.Files.FileColumns.MEDIA_TYPE,
//				MediaStore.Files.FileColumns.MIME_TYPE,
//				MediaStore.Files.FileColumns.TITLE
			)

			// Return only video and image metadata.
			val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
					+ MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
					+ " OR "
					+ MediaStore.Files.FileColumns.MEDIA_TYPE + "="
					+ MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

			val queryUri = MediaStore.Files.getContentUri("external")

			cursor = contentResolver.query(
				queryUri,
				projection,
				null,
				null,
				MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
			)


			if(cursor != null && cursor.count > 0){
				Log.e(TAG, "getFilePathFromContentUri: " +cursor.count )
				var int_position = 0
				var absolutePathOfImage: String? = null
				var image_type = ""
				var thumb_img_path: String
				var name: String
				var date_added: Long
				var img_size: Long
				var column_index_data: Int
				var column_index_folder_name = 0
				var id: Long

				val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
				column_index_data = cursor.getColumnIndexOrThrow(MediaColumns.DATA)
				val img_type = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
				val thumb_index = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA)
//				val doc_index = cursor.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)

//				Log.e(TAG, "getFilePathFromContentUri_000: " + cursor.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME) )

				val width_index = cursor.getColumnIndex(MediaColumns.WIDTH)
				val height_index = cursor.getColumnIndex(MediaColumns.HEIGHT)
				val size_index = cursor.getColumnIndex(MediaColumns.SIZE)
				val display_name_index = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
				val date_index = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)

				if (img_type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
					column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)
				}

				if (img_type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
					column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME)
				}

				if (img_type == MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT) {

					column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
					Log.e(TAG, "getFilePathFromContentUri_document: $column_index_folder_name")
				}
                if (img_type == MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO) {
					column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
				}

				//column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
				while (cursor.moveToNext()) {

//					if(cursor.getString(column_index_data).contains(selectedVideoUri)){
						id = cursor.getLong(idColumn)
						absolutePathOfImage = cursor.getString(column_index_data)
						thumb_img_path = cursor.getString(thumb_index)
						image_type = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))
						Debug.e("Column=>Column", absolutePathOfImage)
						//Debug.e("Column=>Folder", cursor.getString(column_index_folder_name));
						Debug.e("Column=>Media Type=>", image_type)
						img_size = cursor.getLong(size_index)
						name = cursor.getString(display_name_index)
						date_added = cursor.getLong(date_index) * 1000
						for (i in al_images.indices) {
							//Debug.e("compare_folder==>", al_images.get(i).getStr_folder() + "___" + cursor.getString(column_index_folder_name));
							if (al_images[i].str_folder.equals(cursor.getString(column_index_folder_name))
							) {
								boolean_folder = true
								int_position = i
								break
							} else {
								boolean_folder = false
							}
							Debug.e(TAG, "boolean_folder==>$boolean_folder")
						}
						val image: Model_images.Image = Model_images.Image()
						if (image_type.toInt() == 3) {

							//Debug.e("Column_specific=>Duration=>", "" + cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)));
							image.video_duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION))
//						image.video_duration(cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)))
						}
						else {
							/*Debug.e("Column_specific=>Width=>", cursor.getString(width_index));
                                Debug.e("Column_specific=>Height=>", cursor.getString(height_index));*/
							image.width = cursor.getString(width_index)
							image.height = cursor.getString(height_index)

						}
						if (boolean_folder) {
							Debug.e(
								"folders==>123=>",
								al_images.size.toString() + ""
							)
							if (al_images.size > 0) {
								val al_path: ArrayList<Model_images.Image> =
									ArrayList<Model_images.Image>()
								al_images.get(
									int_position
								).al_imagepath?.let {
									al_path.addAll(
										it
									)
								}
								image.imgPath = absolutePathOfImage
								image.imgIndex = al_path.size
								image.imgType = image_type.toInt()
								image.thumbImgPath = thumb_img_path
								image.size = img_size
								image.name = name
								image.date = date_added
								image.id = id
								image.contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

								al_path.add(image)
								al_images.get(int_position)
									.setAl_imagepath(al_path)
							}
						} else {

							val al_path: ArrayList<Model_images.Image> = ArrayList<Model_images.Image>()

							image.imgPath = absolutePathOfImage
							image.imgIndex = al_path.size
							image.imgType = image_type.toInt()
							image.thumbImgPath = thumb_img_path
							image.size = img_size
							image.name = name
							image.date = date_added
							image.id = id
							image.contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

							al_path.add(image)
							val obj_model = Model_images()
							obj_model.setStr_folder(cursor.getString(column_index_folder_name))
							obj_model.setAl_imagepath(al_path)
							al_images.add(obj_model)
							Debug.e(
								"folders==>456=>",
								al_images.size.toString() + ""
							)
						}
					}

				}
//			}



			for (i in al_images.indices) {
//				Log.e(TAG, "getFilePathFromContentUri: " +al_images[i].al_imagepath )
				al_images[i].al_imagepath?.let {
					al_sub_images.addAll(it) }
			}

			for (i in al_sub_images.indices) {

					al_sub_images[i].thumbImgPath?.let {
						if(al_sub_images[i].thumbImgPath!!.contains(selectedVideoUri)){
							Log.e(TAG, "getFilePathFromContentUri: " +al_sub_images[i].contentUri  + " " + al_sub_images[i].id )
							al_filepath.add(it)
						}
				 }

			}
		} catch (e: Exception) {
//			Log.e(TAG, "getFilePathFromContentUri: " +e.message )
		}
		return  al_filepath;
	}

	@SuppressLint("Range")
	private fun getExternalMediaList(): ArrayList<AllData> {
		allDataList.clear()

		val cr: ContentResolver = context.contentResolver

		val	uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
			MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
		}else{
			MediaStore.Files.getContentUri("external")
		}

		val projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME)
		val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				(MediaStore.Files.FileColumns.MEDIA_TYPE + "="
					 + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE
					 + " OR "
					 + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
					 + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
					 + " OR "
					 + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
					 + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO
					 + " OR "
					 + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
					 + MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT
					 + " OR "
					 + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
					 + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
			}
		else {
				(MediaStore.Files.FileColumns.MEDIA_TYPE + "="
						+ MediaStore.Files.FileColumns.MEDIA_TYPE_NONE
						+ " OR "
						+ MediaStore.Files.FileColumns.MEDIA_TYPE + "="
						+ MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
						+ " OR "
						+ MediaStore.Files.FileColumns.MEDIA_TYPE + "="
						+ MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO
						+ " OR "
						+ MediaStore.Files.FileColumns.MEDIA_TYPE + "="
						+ MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
			}

		val selectionArgs: Array<String>? = null
		val selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
		val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
		val selectionArgsPdf = arrayOf(mimeType)

		val cursor = cr.query(uri, null, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC")!!
		cursor.moveToFirst()
		while (!cursor.isAfterLast) {
			val allData = AllData()

			val columnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
			val mediaId = cursor.getLong(columnIndex)
			val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME))
			val filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
			val dateCreate = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED))
			val dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED))
			val media_type = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE))
			val mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE))

			val width_index = cursor.getColumnIndex(MediaColumns.WIDTH)
			val height_index = cursor.getColumnIndex(MediaColumns.HEIGHT)
//			Log.e(TAG, "getExternalMediaList: " +mediaId )



			if (media_type.toInt() == 3) {

				//Debug.e("Column_specific=>Duration=>", "" + cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)));
				allData.video_duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION))
				allData.size = cursor.getColumnIndex(MediaStore.Video.Media.SIZE).toString()
				allData.fileUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaId).toString()

//						image.video_duration(cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)))
			}
			else if(media_type.toInt() == 1){

				allData.width = cursor.getString(width_index)
				allData.height = cursor.getString(height_index)
				allData.size = cursor.getColumnIndex(MediaStore.Images.Media.SIZE).toString()
				allData.fileUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaId).toString()

			}
			else if(media_type.toInt() == 2 ){
				allData.fileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaId).toString()

			}
			else{
				allData.size = cursor.getColumnIndex(MediaColumns.SIZE).toLong().toString()
				val fileUri = Uri.parse("$uri/$mediaId")
				allData.fileUri = fileUri.toString()
			}
			allData.mediaId = mediaId
			allData.displayName =displayName
			allData.dateCreate = dateCreate
			allData.dateModified = dateModified
			allData.filepath = filePath
			allData.media_type = media_type.toInt()
			allData.mime_type = mime_type
			allDataList.add(allData)

			Log.e(TAG, "getExternalMediaList: " +displayName)


//			uriList.add(FileModel(mediaId,filePath,displayName, fileUri,dateCreate,dateModified))
			cursor.moveToNext()
		}
		cursor.close()

		return allDataList
	}




	@Suppress("SameParameterValue")
	@SuppressLint("SimpleDateFormat")
	private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
		SimpleDateFormat("dd.MM.yyyy").let { formatter ->
			TimeUnit.MICROSECONDS.toSeconds(formatter.parse("$day.$month.$year")?.time ?: 0)
		}

	override fun init(messenger: BinaryMessenger) {
		channel = MethodChannel(messenger, "media_storage")
		channel.setMethodCallHandler(this)
	}

	override fun setActivity(activity: Activity?) {
		this.activity = activity
	}


	private fun haveStoragePermission() = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

	override fun dispose() {
		if (::channel.isInitialized) {
			channel.setMethodCallHandler(null)
		}
	}

	fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<String>,
		grantResults: IntArray
	) :Boolean {

		Log.e(TAG, "onRequestPermissionsResult: " )
		when (requestCode) {
			READ_EXTERNAL_STORAGE_REQUEST -> {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					isPermission = true;
				} else {
					// If we weren't granted the permission, check to see if we should show
					// rationale for the permission.
					val showRationale =
						activity?.let {
							ActivityCompat.shouldShowRequestPermissionRationale(
								it,
								Manifest.permission.READ_EXTERNAL_STORAGE
							)
						}

					/**
					 * If we should show the rationale for requesting storage permission, then
					 * we'll show [ActivityMainBinding.permissionRationaleView] which does this.
					 *
					 * If `showRationale` is false, this means the user has not only denied
					 * the permission, but they've clicked "Don't ask again". In this case
					 * we send the user to the settings page for the app so they can grant
					 * the permission (Yay!) or uninstall the app.
					 */
//					if (showRationale) {
//						showNoAccess()
//					} else {
//						goToSettings()
//					}
					isPermission = false;
				}

			}
		}
		return isPermission;
	}

	private  fun getExternalStoragePublicDirectory(type: String?) : String {
		return Environment.getExternalStoragePublicDirectory(type).toString()
	}

	private fun getExternalStorageDirectories() : ArrayList<String> {
		val appsDir: Array<File> = context.getExternalFilesDirs(null)
		var extRootPaths: ArrayList<String> = ArrayList<String>()
		for (file: File in appsDir)
			extRootPaths.add(file.getAbsolutePath())
		return extRootPaths;
		// return Environment.getExternalStorageDirectory().toString();
	}


	  private fun getMediaStoreData(path: String?) :String {
		  var subRootList: ArrayList<AllData> = ArrayList()

//		  Log.e(TAG, "getMediaStoreData: " +path )
		var extRootPaths_: ArrayList<AllData> = ArrayList()

		  extRootPaths_.isEmpty()
		  subRootList.isEmpty()
//		  extRootPaths = path?.let { getFilePathFromContentUri(it,context.contentResolver)}!!;
//		    extRootPaths =  getPdfList()
		  try {
			  extRootPaths_ =  getExternalMediaList()

//			  if (path != null) {
//				  getFilePathFromContentUri(path,context.contentResolver)
//			  }
//			  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//				  getDownloadFiles(context.contentResolver)
//			  }

			  if(path == null){
			  	subRootList.addAll(extRootPaths_)
			  }else{
				  for (i in extRootPaths_.indices) {
//				  Log.e(TAG, "getExternalMediaList: " +extRootPaths_[i].fileUri )
					  if(extRootPaths_[i].filepath!!.contains(path)){
						  subRootList.add(extRootPaths_[i])
					  }
				  }
			  }

			  Log.e(TAG, "getMediaStoreData: ${subRootList.size}")

		  } catch (e: Exception) {
			  Log.e(TAG, "getMediaStoreData_0909 " + e.message )
		  }
		  if(subRootList.size <= 0){
		  	return ""
		  }else{
			  val listOfStrings = Gson().toJson(subRootList, mutableListOf<AllData>().javaClass)
//			  Log.e(TAG, "getMediaStoreData: " +listOfStrings )

			  return listOfStrings
		  }

	}

	@RequiresApi(Build.VERSION_CODES.Q)
	 fun getDownloadFiles(contentResolver: ContentResolver) {
		val projectionDownloads = arrayOf(
			MediaStore.DownloadColumns._ID,
//			MediaStore.Downloads.DISPLAY_NAME,
		)
		val selectionDownloads =MediaStore.Files.FileColumns.DISPLAY_NAME+"=?";
		val selectionArgsDownloads = emptyArray<String>()
		val sortOrderDownloads = "${MediaStore.Downloads.DISPLAY_NAME} ASC"

		/*contentResolver.query(
			MediaStore.Downloads.EXTERNAL_CONTENT_URI,
			projectionDownloads,
			selectionDownloads,
			selectionArgsDownloads,
			sortOrderDownloads
		)?.use { cursor ->
			Log.e("SeeMedia", "we got cursor! $cursor")
			val idColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)
			val nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)

			while (cursor.moveToNext()) { //Here return false
				Log.e("SeeMedia", "Move to next!")
				val id = cursor.getLong(idColumn)
				val name = cursor.getString(nameColumn)
				Log.e("SeeMedia", "ID = $id AND NAME= $name")
			}
		}
*/

		val cursor = contentResolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI,
			projectionDownloads,
			selectionDownloads,
			null,
			sortOrderDownloads)!!
		cursor.moveToFirst()
		while (!cursor.isAfterLast) {
			val idColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)
			val nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)

//			Log.e("SeeMedia", "Move to next!")
			val id = cursor.getLong(idColumn)
			val name = cursor.getString(nameColumn)
			Log.e("SeeMedia", "ID = $id AND NAME= $name")

//			uriList.add(FileModel(mediaId,filePath,displayName, fileUri,dateCreate,dateModified))
			cursor.moveToNext()
		}
		cursor.close()
	}
}

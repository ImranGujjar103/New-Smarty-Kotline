package com.imr.example.newsmartykotlin.core.permission

import android.Manifest
import android.os.Build
import com.imr.example.newsmartykotlin.R

data class AppPermissionInfo(
    val type: AppPermissionType,
    val permission: String?,
    val titleRes: Int,
    val messageRes: Int,
    val iconRes: Int,
    val settingMessageRes: Int
)

object AppPermissionRegistry {

/*
    fun get(type: AppPermissionType): AppPermissionInfo {
        return when (type) {
            AppPermissionType.NOTIFICATION -> AppPermissionInfo(
                type = type,
                permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.POST_NOTIFICATIONS
                } else null,
                titleRes = R.string.notification_permission_title,
                messageRes = R.string.notification_permission_message,
                settingMessageRes = R.string.notification_setting_permission_message,
                iconRes = R.drawable.ic_permission_notification
            )

            AppPermissionType.MICROPHONE -> AppPermissionInfo(
                type = type,
                permission = Manifest.permission.RECORD_AUDIO,
                titleRes = R.string.microphone_permission_title,
                messageRes = R.string.microphone_permission_message,
                settingMessageRes = R.string.microphone_setting_permission_message,
                iconRes = R.drawable.ic_permission_microphone
            )

            AppPermissionType.CAMERA -> AppPermissionInfo(
                type = type,
                permission = Manifest.permission.CAMERA,
                titleRes = R.string.camera_permission_title,
                messageRes = R.string.camera_permission_message,
                settingMessageRes = R.string.camera_setting_permission_message,
                iconRes = R.drawable.ic_permission_microphone
            )

            AppPermissionType.STORAGE -> AppPermissionInfo(
                type = type,
                permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                },
                titleRes = R.string.storage_permission_title,
                messageRes = R.string.storage_permission_message,
                settingMessageRes = R.string.storage_setting_permission_message,
                iconRes = R.drawable.ic_permission_microphone
            )
        }
    }
*/
}
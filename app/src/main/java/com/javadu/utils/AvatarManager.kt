package com.javadu.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class AvatarManager(private val context: Context) {

    private val avatarDir: File by lazy {
        File(context.filesDir, AVATAR_DIR).apply {
            if (!exists()) mkdirs()
        }
    }

    fun saveAvatar(sourceUri: Uri): String {
        val avatarFile = File(avatarDir, "avatar_${System.currentTimeMillis()}.jpg")
        
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(avatarFile).use { output ->
                input.copyTo(output)
            }
        }
        
        return avatarFile.absolutePath
    }

    fun deleteAvatar() {
        avatarDir.listFiles()?.forEach { it.delete() }
    }

    companion object {
        private const val AVATAR_DIR = "avatars"
    }
}

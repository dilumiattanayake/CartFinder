package com.sjay.cartfinder.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberAsyncImagePainter
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import android.util.Base64

@Composable
fun rememberSafeImagePainter(imageUrl: String?): Painter {
    if (imageUrl == null) {
        return rememberAsyncImagePainter(model = null)
    }
    
    if (imageUrl.startsWith("data:image")) {
        val bitmapPainter = remember(imageUrl) {
            try {
                val base64String = imageUrl.substringAfter("base64,")
                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                if (bitmap != null) {
                    BitmapPainter(bitmap.asImageBitmap())
                } else null
            } catch (e: Exception) {
                null
            }
        }
        
        if (bitmapPainter != null) {
            return bitmapPainter
        }
    }
    
    return rememberAsyncImagePainter(model = imageUrl)
}

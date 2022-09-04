package ds.tetris.util

import android.content.Context
import android.os.ParcelFileDescriptor
import java.io.FileDescriptor

fun Context.getRawResource(filename: String) {
    val resourceId = resources.getIdentifier(
        filename.substringBefore("."), "raw", packageName
    )
/*    FileDescriptor()
    loadXmlImageVector()
    resources.openRawResource(resourceId).*/
}
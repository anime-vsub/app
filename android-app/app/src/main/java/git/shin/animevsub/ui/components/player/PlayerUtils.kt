package git.shin.animevsub.ui.components.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun findActivity(context: Context): Activity? = when (context) {
  is Activity -> context
  is ContextWrapper -> findActivity(context.baseContext)
  else -> null
}

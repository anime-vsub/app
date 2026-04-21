package git.shin.animevsub.ui.components.dialogs

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

@Composable
fun DonationDialog(
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val currentYear = Calendar.getInstance().get(Calendar.YEAR)
  val yearsActive = currentYear - 2022

  val saveQrToGallery = {
    try {
      val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.timo_qr)
      val filename = "AnimeVsub_Donation_QR_${System.currentTimeMillis()}.jpg"
      val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
          put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
          put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
          put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/AnimeVsub")
        }
        val imageUri: Uri? = context.contentResolver.insert(
          MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
          contentValues
        )
        imageUri?.let { context.contentResolver.openOutputStream(it) }
      } else {
        val imagesDir = android.os.Environment.getExternalStoragePublicDirectory(
          android.os.Environment.DIRECTORY_PICTURES
        ).toString() + File.separator + "AnimeVsub"
        val file = File(imagesDir)
        if (!file.exists()) file.mkdirs()
        val image = File(file, filename)
        FileOutputStream(image)
      }

      outputStream?.use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        Toast.makeText(context, context.getString(R.string.donation_qr_saved), Toast.LENGTH_SHORT).show()
      }
    } catch (e: Exception) {
      Toast.makeText(context, context.getString(R.string.donation_save_error, e.message), Toast.LENGTH_SHORT).show()
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFF1A1A1A),
    shape = RoundedCornerShape(24.dp),
    title = {
      Text(
        stringResource(R.string.donation_title),
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    },
    text = {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          stringResource(R.string.donation_description, yearsActive),
          color = Color.LightGray,
          textAlign = TextAlign.Center,
          lineHeight = 22.sp
        )

        Text(
          stringResource(R.string.donation_message),
          color = Color.White,
          fontWeight = FontWeight.Medium,
          textAlign = TextAlign.Center
        )

        // QR Code
        Box(
          modifier = Modifier
            .size(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(4.dp)
        ) {
          Image(
            painter = painterResource(id = R.drawable.timo_qr),
            contentDescription = stringResource(R.string.donation_qr_desc),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
          )

          // Nút Lưu QR
          IconButton(
            onClick = { saveQrToGallery() },
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(8.dp)
              .background(Color.Black.copy(alpha = 0.6f), CircleShape)
              .size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = stringResource(R.string.donation_save_qr),
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Text(
          stringResource(R.string.donation_bank_info, "Timo (BVBank)", "9021454964386", "NGUYEN TIEN THANH"),
          color = Color.Cyan,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(stringResource(R.string.donation_got_it), color = Color.White)
      }
    }
  )
}

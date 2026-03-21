package eu.org.animevsub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import eu.org.animevsub.ui.AnimeVsubAppUI
import eu.org.animevsub.ui.theme.AnimeVsubTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimeVsubTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimeVsubAppUI()
                }
            }
        }
    }
}

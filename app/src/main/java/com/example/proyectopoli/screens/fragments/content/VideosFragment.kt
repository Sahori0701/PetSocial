package com.example.proyectopoli.screens.fragments.content

import android.media.MediaPlayer
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.proyectopoli.R

@Composable
@Preview
fun VideosFragment() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFFEFEFF),
                        Color(0xFFFDFEFF)
                    )
                )
            ),
        contentAlignment = Alignment.TopStart
    ) {
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(0.1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.app_title_video),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.cat_user),
                        contentDescription = "Foto de perfil usuario",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(0.2f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.app_video),
                    style = MaterialTheme.typography.bodyMedium
                        .copy(fontSize = 18.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            var isVideo1Expanded by remember { mutableStateOf(false) }
            var isVideo2Expanded by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Video 1 (izquierda)
                val context1 = LocalContext.current
                AndroidView(
                    modifier = Modifier
                        .width(if (isVideo1Expanded) 800.dp else 180.dp)
                        .height(if (isVideo1Expanded) 900.dp else 200.dp)
                        .offset(x = (-1).dp, y = 6.dp)
                        .clickable { isVideo1Expanded = !isVideo1Expanded }
                        .animateContentSize(),
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            val videoPath = "android.resource://${ctx.packageName}/${R.raw.videogato}"
                            val uri = Uri.parse(videoPath)
                            uri?.let { setVideoURI(it) } ?: println("Error: Uri nulo para el video en $videoPath")
                            val mediaController = MediaController(ctx)
                            mediaController.setAnchorView(this)
                            setMediaController(mediaController)
                            setOnPreparedListener { mp ->
                                mp.setOnVideoSizeChangedListener { _, _, _ ->
                                    setMediaController(mediaController)
                                    mediaController.setAnchorView(this)
                                }
                            }
                        }
                    },
                    update = { _ -> }
                )

                // Video 2 (derecha)
                val context2 = LocalContext.current
                AndroidView(
                    modifier = Modifier
                        .width(if (isVideo2Expanded) 800.dp else 180.dp)
                        .height(if (isVideo2Expanded) 900.dp else 200.dp)
                        .offset(x = -1.dp, y = 6.dp)
                        .clickable { isVideo2Expanded = !isVideo2Expanded }
                        .animateContentSize(),
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            val videoPath = "android.resource://${ctx.packageName}/${R.raw.domino}"
                            val uri = Uri.parse(videoPath)
                            uri?.let { setVideoURI(it) } ?: println("Error: Uri nulo para el video en $videoPath")
                            val mediaController = MediaController(ctx)
                            mediaController.setAnchorView(this)
                            setMediaController(mediaController)
                            setOnPreparedListener { mp ->
                                mp.setOnVideoSizeChangedListener { _, _, _ ->
                                    setMediaController(mediaController)
                                    mediaController.setAnchorView(this)
                                }
                            }
                        }
                    },
                    update = { _ -> }
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .offset(y = 80.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.raster_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(87.dp)
                        .width(100.dp)
                )
                Text(
                    text = stringResource(R.string.raster_string),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
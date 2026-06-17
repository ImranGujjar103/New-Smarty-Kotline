package com.imr.example.newsmartykotlin.presentation.passport.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.toUri
import com.imr.example.newsmartykotlin.ui.theme.TextColor

@Composable
fun PassportPreviewBox(
    imageRes: Int,
    imageUri: String?,
    pixelText: String,
    inchText: String
) {
    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center){
        Box(
            modifier = Modifier
                .height(208.dp).width(249.dp),
            contentAlignment = Alignment.Center
        )
        {
            Image(
                painter = if (imageUri.isNullOrBlank()) {
                    painterResource(imageRes)
                } else {
                    rememberAsyncImagePainter(imageUri.toUri())
                },
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )

            // Top horizontal line
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    text = pixelText.substringBefore(" x").trim()+" inch",
                    fontSize = 9.sp,
                    color = TextColor,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 18.dp)
                        .width(150.dp)
                        .height(1.dp)
                        .background(TextColor.copy(alpha = 0.6f))
                )
            }

            // Bottom horizontal line
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(150.dp)
                        .height(1.dp)
                        .background(TextColor.copy(alpha = 0.6f))
                )

                Text(
                    text = inchText.substringBefore(" x").trim() +" inch",
                    fontSize = 8.sp,
                    color = TextColor,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }

            // Left vertical line
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)

            ) {
                Text(
                    text = pixelText.substringAfter("x").trim(),
                    fontSize = 9.sp,
                    color = TextColor,
                    modifier = Modifier
                        .align(Alignment.CenterEnd).padding(end = 6.dp)

                )

                Box(
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .width(1.dp)
                        .height(150.dp)
                        .background(TextColor.copy(alpha = 0.6f))
                )
            }

            // Right vertical line
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(150.dp)
                        .background(TextColor.copy(alpha = 0.6f))
                )

                Text(
                    text = inchText.substringAfter("x").trim(),
                    fontSize = 9.sp,
                    color = TextColor,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .align(Alignment.CenterEnd)
                )
            }
        }
    }

}
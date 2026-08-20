package com.imr.example.newsmartykotlin.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExitBottomSheet(
    isRated: Boolean,
    onDismiss: () -> Unit,
    onExit: () -> Unit,
    onRateUs: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WhiteColor,
        scrimColor = Color.Black.copy(alpha = 0.4f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.exit_confirmation_title),
                    color = TextColor,
                    fontSize = 14.sp,
                    lineHeight = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.montserrat_bold))
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onExit,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CardColor,
                            contentColor = TextColor
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.exit),
                            fontSize = 14.sp,
                            lineHeight = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.montserrat_bold))
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CardColor,
                            contentColor = TextColor
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontSize = 14.sp,
                            lineHeight = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.montserrat_bold))
                        )
                    }
                }
            }

            if (!isRated) {
                RatingCard(onRateUs = onRateUs)
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RatingCard(onRateUs: (Int) -> Unit) {
    var currentRating by remember { mutableFloatStateOf(3f) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(0.dp) // Flat bottom as it covers the rest of the sheet
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.enjoying_app_title),
                color = TextColor,
                fontSize = 14.sp,
                lineHeight = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.montserrat_bold))
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.how_would_you_rate_app),
                color = TextColor,
                fontSize = 11.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp,
                fontFamily = FontFamily(Font(R.font.montserrat_medium))
            )

            Spacer(modifier = Modifier.height(18.dp))

            RatingBar(
                rating = currentRating,
                onRatingChanged = { currentRating = it }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.7f).padding(start = 40.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End
            )
            {
                Text(
                    text = stringResource(R.string.the_best_we_can_get),
                    color = TextColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_medium))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Canvas(modifier = Modifier.size(30.dp, 20.dp)) {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.5f)
                        quadraticTo(size.width * 0.5f, size.height * 0.6f, size.width * 0.8f, 0f)
                    }
                    drawPath(
                        path = path,
                        color = TextColor,
                        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Arrowhead
                    val arrowHeadPath = Path().apply {
                        moveTo(size.width * 0.6f, size.height * 0.2f)
                        lineTo(size.width * 0.8f, 0f)
                        lineTo(size.width * 0.95f, size.height * 0.25f)
                    }
                    drawPath(
                        path = arrowHeadPath,
                        color = TextColor,
                        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onRateUs(ceil(currentRating).toInt()) },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.rate_us),
                    color = WhiteColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.montserrat_bold))
                )
            }
        }
    }
}

@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val starWidth = size.width / 5
                    val newRating = (offset.x / starWidth).coerceIn(0f, 5f)
                    onRatingChanged(newRating)
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    val starWidth = size.width / 5
                    val newRating = (change.position.x / starWidth).coerceIn(0f, 5f)
                    onRatingChanged(newRating)
                }
            },
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (i in 1..5) {
            StarIcon(
                isSelected = i <= ceil(rating),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun StarIcon(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val starColor = if (isSelected) Color(0xFFFF9D00) else Color.White
    
    Canvas(modifier = modifier) {
        val path = Path()
        val centerX = size.width / 2
        val centerY = size.height / 2
        val outerRadius = size.width / 2
        val innerRadius = outerRadius / 2.5f
        
        val angleStep = PI / 5
        
        for (i in 0 until 10) {
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            val angle = i * angleStep - PI / 2
            val x = (centerX + radius * cos(angle)).toFloat()
            val y = (centerY + radius * sin(angle)).toFloat()
            
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        
        drawPath(path = path, color = starColor)
        
        if (!isSelected) {
            drawPath(
                path = path, 
                color = Color(0xFFE5E4E4), 
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

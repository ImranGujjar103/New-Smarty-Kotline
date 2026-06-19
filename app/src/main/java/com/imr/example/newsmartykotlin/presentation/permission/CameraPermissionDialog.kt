package com.imr.example.newsmartykotlin.presentation.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayRegular
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

@Composable
fun CameraPermissionDialog(
    onCancelClick: () -> Unit,
    onAllowClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancelClick,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = WhiteColor,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 20.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.camera_permission_required),
                    fontFamily = SfProDisplayBold,
                    fontSize = 16.sp,
                    color = TextColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.to_take_photos_for_your_passport_we_need_access_to_your_camera_please_grant_permission_to_continue),
                    fontSize = 12.sp,
                    fontFamily = SfProDisplayRegular,
                    color = TextColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DialogSecondaryButton(
                        text = stringResource(R.string.deny),
                        onClick = onCancelClick,
                        modifier = Modifier.weight(1f)
                    )

                    DialogPrimaryButton(
                        text = stringResource(R.string.allow),
                        onClick = onAllowClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                color = CardColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = SfProDisplayBold,
            fontSize = 14.sp,
            color = TextColor
        )
    }
}

@Composable
private fun DialogPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                color = PrimaryColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = SfProDisplayBold,
            fontSize = 14.sp,
            color = WhiteColor
        )
    }
}

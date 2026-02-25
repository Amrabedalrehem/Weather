package com.example.presentation.component.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.data.model.entity.AlarmEntity
import com.example.weather.R
import com.example.presentation.component.helper.toArabicDigits
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun AlarmCard(
    alarm: AlarmEntity,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val formattedTime = SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale.getDefault())
        .format(alarm.timeInMillis)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(alarm.city, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(formattedTime.toArabicDigits(), fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
                val typeLabel = when (alarm.type) {
                    "Alert" -> androidx.compose.ui.res.stringResource(R.string.alert)
                    "Notification" -> androidx.compose.ui.res.stringResource(R.string.notification)
                    else -> alarm.type
                }
                Text(typeLabel, fontSize = 12.sp, color = Color(0xFF3B82F6))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pencilwrite))
                    LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever, modifier = Modifier.size(50.dp))                              }
                IconButton(onClick = onDelete) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.minuscircle))
                    LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever, modifier = Modifier.size(50.dp))                }
            }
        }
    }
}

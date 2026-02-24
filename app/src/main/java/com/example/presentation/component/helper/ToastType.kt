package com.example.presentation.component.helper

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
 import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class ToastType { SUCCESS, ERROR, INFO, WARNING }

data class ToastData(
    val message: String,
    val type: ToastType = ToastType.INFO
)

@Composable
fun rememberToastState(): ToastState {
    return remember { ToastState() }
}

class ToastState {
    var toastData by mutableStateOf<ToastData?>(null)
        private set

    fun show(message: String, type: ToastType = ToastType.INFO) {
        toastData = ToastData(message, type)
    }

    fun dismiss() {
        toastData = null
    }
}

@Composable
fun CustomToast(state: ToastState) {
    val data = state.toastData

    LaunchedEffect(data) {
        if (data != null) {
            delay(3000)
            state.dismiss()
        }
    }

    Box(
        modifier         = Modifier.fillMaxSize().padding(bottom = 64.dp).padding(horizontal = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = data != null,
            enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit    = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            data?.let {
                val backgroundColor = when (it.type) {
                    ToastType.SUCCESS -> Color(0xFF4CAF50)
                    ToastType.ERROR   -> Color(0xFFE53935)
                    ToastType.INFO    -> Color(0xFF2196F3)
                    ToastType.WARNING -> Color(0xFFFF9800)
                }

                val icon = when (it.type) {
                    ToastType.SUCCESS -> Icons.Filled.CheckCircle
                    ToastType.ERROR   -> Icons.Filled.Warning
                    ToastType.INFO    -> Icons.Filled.Info
                    ToastType.WARNING -> Icons.Filled.Warning
                }

                Card(
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = backgroundColor),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier  = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier              = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text       = it.message,
                            color      = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp
                        )
                    }
                }
            }
        }
    }
}
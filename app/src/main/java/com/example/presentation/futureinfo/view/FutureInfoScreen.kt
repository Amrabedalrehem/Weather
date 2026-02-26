package com.example.presentation.futureinfo.view
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.presentation.futureinfo.viewmodel.BadWeatherDay
import com.example.presentation.futureinfo.viewmodel.FutureInfoState
import com.example.presentation.component.futureinfo.SummaryBanner
import com.example.presentation.component.futureinfo.AnimatedDayCard
import com.example.presentation.futureinfo.viewmodel.FutureInfoViewModel
import com.example.weather.R
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutureInfoScreen(
    viewModel: FutureInfoViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchForecast() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = com.example.presentation.theme.LocalWeatherGradient.current
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = stringResource(R.string.upcoming_bad_weather),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        when (val s = state) {
            is FutureInfoState.Loading -> LoadingContent()
            is FutureInfoState.Error   -> ErrorContent()
            is FutureInfoState.Success -> {
                if (s.days.isEmpty()) {
                    AllClearContent()
                } else {
                    SuccessContent(days = s.days)
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF3B82F6))
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.checking_days), color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    }
}

@Composable
private fun ErrorContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠️", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.could_not_load_forecast), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.check_connection), color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun AllClearContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.notification))
            LottieAnimation(
                composition = composition,
                iterations  = LottieConstants.IterateForever,
                modifier    = Modifier.size(180.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text       = stringResource(R.string.all_clear),
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text      = stringResource(R.string.no_bad_weather_5days),
                fontSize  = 15.sp,
                color     = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SuccessContent(days: List<BadWeatherDay>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))

        SummaryBanner(days)

        Spacer(Modifier.height(20.dp))

        Text(
            text       = stringResource(R.string.detailed_forecast),
            fontSize   = 14.sp,
            color      = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.SemiBold,
            modifier   = Modifier.padding(bottom = 10.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(days) { index, day ->
                AnimatedDayCard(day = day, index = index)
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

package com.example.presentation.navigation
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val navBg         = MaterialTheme.colorScheme.surface
    val primaryColor  = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val onSurface     = MaterialTheme.colorScheme.onSurface

    val activeGradient = Brush.linearGradient(listOf(primaryColor, tertiaryColor))

    val glowLine = Brush.horizontalGradient(
        listOf(Color.Transparent, primaryColor.copy(0.55f), tertiaryColor.copy(0.45f), Color.Transparent)
    )
    val activeIconBg = Brush.linearGradient(
        listOf(primaryColor.copy(0.16f), tertiaryColor.copy(0.13f))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {

         Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter)
                .drawBehind {
                     drawLine(
                        brush = glowLine,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.2.dp.toPx()
                    )
                },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = navBg),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                navigationItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.route == item.route::class.qualifiedName
                    } == true

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(item.titleResId),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) onSurface else onSurface.copy(alpha = 0.38f)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(4.dp))
                             Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(2.dp)
                                    .background(
                                        brush = activeGradient,
                                        shape = RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }

         Row(
            modifier = Modifier
                .fillMaxWidth()
                 .padding(top = 4.dp, start = 8.dp, end = 8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            navigationItems.forEach { item ->
                val isSelected = currentDestination?.hierarchy?.any {
                    it.route == item.route::class.qualifiedName
                } == true

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = if (isSelected) activeIconBg
                            else Brush.linearGradient(
                                listOf(
                                    onSurface.copy(alpha = 0.05f),
                                    onSurface.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .then(
                            if (isSelected) Modifier.border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    listOf(primaryColor.copy(0.5f), tertiaryColor.copy(0.45f))
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) else Modifier.border(
                                width = 1.dp,
                                color = onSurface.copy(alpha = 0.07f),
                                shape = RoundedCornerShape(14.dp)
                            )
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isSelected) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = stringResource(item.titleResId),
                        modifier = Modifier.size(22.dp),
                         tint = if (isSelected) primaryColor else onSurface.copy(alpha = 0.35f)
                    )
                }
            }
        }
    }
}
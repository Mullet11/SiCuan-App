package com.example.sicuan.presentation.screen.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// Unused imports removed
import com.example.sicuan.R
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.ui.theme.SiCuanDimens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var isLogoVisible by remember { mutableStateOf(false) }
    var isTextVisible by remember { mutableStateOf(false) }
    var isButtonVisible by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceOffset"
    )

    val context = LocalContext.current
    val mediaPlayer = remember {
        android.media.MediaPlayer.create(context, R.raw.splash_sound)
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    LaunchedEffect(Unit) {
        delay(200)
        isLogoVisible = true
        // Play sound when logo appears
        mediaPlayer?.start()
        delay(400)
        isTextVisible = true
        delay(1200) // Tunggu lagu berjalan sebelum memunculkan tombol
        isButtonVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = isLogoVisible,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(animationSpec = tween(800))
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_sicuan),
                contentDescription = "Logo SiCuan",
                modifier = Modifier
                    .size(150.dp)
                    .offset(y = offsetY.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(SiCuanDimens.SpacingXl))
        
        AnimatedVisibility(
            visible = isTextVisible,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                initialOffsetY = { 30 },
                animationSpec = tween(800)
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SiCuan",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(SiCuanDimens.SpacingSm))

                Text(
                    text = "Catat cuan, atur masa depan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(72.dp))

        AnimatedVisibility(
            visible = isButtonVisible,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                initialOffsetY = { 30 },
                animationSpec = tween(800)
            )
        ) {
            SiCuanPrimaryButton(
                text = "Mulai Sekarang",
                onClick = onSplashFinished,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}
package com.example.sicuan.presentation.screen.ocr

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.sicuan.data.ocr.ReceiptOcrParser
import com.example.sicuan.domain.model.TransactionCategory
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.utils.PdfHelper
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanReceiptScreen(
    transactionErrorMessage: String?,
    onClearMessage: () -> Unit,
    onNavigateToManualInput: () -> Unit,
    onNavigateToAddTransactionWithData: (
        amount: String?,
        title: String?,
        note: String?,
        merchant: String?,
        dateMillis: Long?,
        category: String?
    ) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var isScanning by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
        }
    }

    fun processInputImage(inputImage: InputImage) {
        isScanning = true
        onClearMessage()

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val text = visionText.text
                if (text.isBlank()) {
                    isScanning = false
                    // TODO: show error toast or message
                    return@addOnSuccessListener
                }

                val parsedResult = ReceiptOcrParser.parse(text)
                isScanning = false

                val categoryStr = parsedResult.category ?: TransactionCategory.getDefaultCategoryByType(TransactionType.EXPENSE)

                onNavigateToAddTransactionWithData(
                    parsedResult.amountText,
                    parsedResult.title,
                    parsedResult.note,
                    parsedResult.merchant,
                    parsedResult.dateMillis,
                    categoryStr
                )
            }
            .addOnFailureListener { error ->
                isScanning = false
                Log.e("OCR", "Error: ${error.message}")
            }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val image = InputImage.fromFilePath(context, uri)
                processInputImage(image)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                isScanning = true
                val bitmap = PdfHelper.renderPdfToBitmap(context, uri)
                if (bitmap != null) {
                    val image = InputImage.fromBitmap(bitmap, 0)
                    processInputImage(image)
                } else {
                    isScanning = false
                    // Error parsing PDF
                }
            }
        }
    }

    fun takePhoto() {
        cameraController.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val mediaImage = image.image
                    if (mediaImage != null) {
                        val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                        processInputImage(inputImage)
                    }
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("Camera", "Capture failed: ${exception.message}")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Transaksi", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Help Info */ }) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Bantuan", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (hasCameraPermission) {
                // Camera Preview
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            this.controller = cameraController
                            cameraController.bindToLifecycle(lifecycleOwner)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Scrim & Overlay
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val rectWidth = canvasWidth * 0.8f
                    val rectHeight = canvasHeight * 0.6f
                    val rectLeft = (canvasWidth - rectWidth) / 2
                    val rectTop = (canvasHeight - rectHeight) / 2 - 50.dp.toPx()

                    val scanRect = Rect(Offset(rectLeft, rectTop), Size(rectWidth, rectHeight))
                    val cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())

                    // Draw dark scrim over everything except the scan box
                    val backgroundPath = Path().apply {
                        addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                        addRoundRect(androidx.compose.ui.geometry.RoundRect(
                            rect = scanRect,
                            topLeft = cornerRadius,
                            topRight = cornerRadius,
                            bottomLeft = cornerRadius,
                            bottomRight = cornerRadius
                        ))
                        fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
                    }

                    drawPath(
                        path = backgroundPath,
                        color = Color.Black.copy(alpha = 0.5f)
                    )

                    // Draw the green corner brackets
                    val bracketLength = 40.dp.toPx()
                    val strokeWidth = 4.dp.toPx()
                    val greenColor = Color(0xFF4CAF50)

                    // Top Left
                    drawLine(greenColor, Offset(rectLeft, rectTop), Offset(rectLeft + bracketLength, rectTop), strokeWidth)
                    drawLine(greenColor, Offset(rectLeft, rectTop), Offset(rectLeft, rectTop + bracketLength), strokeWidth)

                    // Top Right
                    drawLine(greenColor, Offset(rectLeft + rectWidth, rectTop), Offset(rectLeft + rectWidth - bracketLength, rectTop), strokeWidth)
                    drawLine(greenColor, Offset(rectLeft + rectWidth, rectTop), Offset(rectLeft + rectWidth, rectTop + bracketLength), strokeWidth)

                    // Bottom Left
                    drawLine(greenColor, Offset(rectLeft, rectTop + rectHeight), Offset(rectLeft + bracketLength, rectTop + rectHeight), strokeWidth)
                    drawLine(greenColor, Offset(rectLeft, rectTop + rectHeight), Offset(rectLeft, rectTop + rectHeight - bracketLength), strokeWidth)

                    // Bottom Right
                    drawLine(greenColor, Offset(rectLeft + rectWidth, rectTop + rectHeight), Offset(rectLeft + rectWidth - bracketLength, rectTop + rectHeight), strokeWidth)
                    drawLine(greenColor, Offset(rectLeft + rectWidth, rectTop + rectHeight), Offset(rectLeft + rectWidth, rectTop + rectHeight - bracketLength), strokeWidth)
                }

                // Instruction Text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Posisikan struk belanja di dalam kotak",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Capture Button
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 140.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                            .border(4.dp, Color(0xFFA5D6A7), CircleShape)
                            .clickable { takePhoto() },
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner circle design or just blank
                    }
                }
            } else {
                // No permission
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Izin kamera diperlukan untuk fitur ini", color = Color.White)
                }
            }

            // Bottom Action Panel
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1B3026))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButton(
                        icon = Icons.Default.Image,
                        text = "Unggah\nGambar",
                        onClick = { imagePickerLauncher.launch("image/*") }
                    )
                    ActionButton(
                        icon = Icons.Default.PictureAsPdf,
                        text = "Unggah\nPDF",
                        onClick = { pdfPickerLauncher.launch("application/pdf") }
                    )
                    ActionButton(
                        icon = Icons.Default.Edit,
                        text = "Input\nManual",
                        onClick = onNavigateToManualInput
                    )
                }
            }

            // Loading Overlay
            if (isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Memproses struk...", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp)
            .width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            minLines = 2
        )
    }
}
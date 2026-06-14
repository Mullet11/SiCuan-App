package com.example.sicuan.presentation.screen.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sicuan.R
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    currentName: String,
    currentUsername: String,
    currentEmail: String,
    currentPhone: String,
    currentDob: String,
    currentBio: String,
    photoUrl: String?,
    onSave: (String, String, String, String, String, String, String?) -> Unit,
    onPhotoSelected: (Uri) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var username by remember { mutableStateOf(currentUsername.removePrefix("@")) }
    var email by remember { mutableStateOf(currentEmail) }
    var phone by remember { mutableStateOf(currentPhone) }
    var dob by remember { mutableStateOf(currentDob) }
    var bio by remember { mutableStateOf(currentBio) }
    var localPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCountryCode by remember { mutableStateOf("+62") }
    var expandedCountryCode by remember { mutableStateOf(false) }
    var showPhotoBottomSheet by remember { mutableStateOf(false) }
    var isPhotoDeleted by remember { mutableStateOf(false) }
    val countryCodes = listOf("🇮🇩 +62", "🇺🇸 +1", "🇬🇧 +44", "🇯🇵 +81", "🇸🇬 +65", "🇲🇾 +60")

    val hasChanges = name != currentName || username != currentUsername.removePrefix("@") || email != currentEmail || phone != currentPhone || dob != currentDob || bio != currentBio || localPhotoUri != null || (isPhotoDeleted && photoUrl != null)

    val darkBackground = MaterialTheme.colorScheme.background
    val inputBackground = Color.Transparent
    val primaryText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val buttonColor = MaterialTheme.colorScheme.primary
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Setup Cropper
    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            localPhotoUri = result.uriContent
            isPhotoDeleted = false
            result.uriContent?.let { onPhotoSelected(it) }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dob = formatter.format(Date(it))
                    }
                    showDatePicker = false
                }) {
                    Text("Pilih", color = buttonColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    titleContentColor = primaryText,
                    headlineContentColor = primaryText,
                    weekdayContentColor = secondaryText,
                    subheadContentColor = secondaryText,
                    yearContentColor = primaryText,
                    currentYearContentColor = buttonColor,
                    selectedYearContainerColor = buttonColor,
                    selectedYearContentColor = Color.White,
                    dayContentColor = primaryText,
                    selectedDayContainerColor = buttonColor,
                    selectedDayContentColor = Color.White,
                    todayDateBorderColor = buttonColor,
                    todayContentColor = buttonColor
                )
            )
        }
    }

    if (showPhotoBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoBottomSheet = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant, // Tampilan estetik
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = if (isPhotoDeleted) R.drawable.ic_launcher_background else (localPhotoUri ?: photoUrl ?: R.drawable.ic_launcher_background),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                }
                
                HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))

                BottomSheetMenuItem(
                    icon = Icons.Default.Image,
                    text = "Pilih dari galeri",
                    onClick = {
                        showPhotoBottomSheet = false
                        val options = CropImageContractOptions(
                            uri = null,
                            cropImageOptions = CropImageOptions(
                                imageSourceIncludeGallery = true,
                                imageSourceIncludeCamera = false,
                                cropShape = CropImageView.CropShape.OVAL,
                                fixAspectRatio = true,
                                aspectRatioX = 1,
                                aspectRatioY = 1
                            )
                        )
                        cropImageLauncher.launch(options)
                    }
                )
                BottomSheetMenuItem(
                    icon = Icons.Default.CameraAlt,
                    text = "Ambil foto",
                    onClick = {
                        showPhotoBottomSheet = false
                        val options = CropImageContractOptions(
                            uri = null,
                            cropImageOptions = CropImageOptions(
                                imageSourceIncludeGallery = false,
                                imageSourceIncludeCamera = true,
                                cropShape = CropImageView.CropShape.OVAL,
                                fixAspectRatio = true,
                                aspectRatioX = 1,
                                aspectRatioY = 1
                            )
                        )
                        cropImageLauncher.launch(options)
                    }
                )
                BottomSheetMenuItem(
                    icon = Icons.Default.Delete,
                    text = "Hapus foto saat ini",
                    textColor = MaterialTheme.colorScheme.error,
                    iconColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        showPhotoBottomSheet = false
                        localPhotoUri = null
                        isPhotoDeleted = true
                    }
                )
            }
        }
    }

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil", color = primaryText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBackground)
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = hasChanges,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(modifier = Modifier.padding(24.dp).background(darkBackground)) {
                    Button(
                        onClick = { 
                            val finalPhoto = if (isPhotoDeleted) "" else (localPhotoUri?.toString() ?: photoUrl)
                            onSave(name, username, email, phone, dob, bio, finalPhoto) 
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Picture Section
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = if (isPhotoDeleted) R.drawable.ic_launcher_background else (localPhotoUri ?: photoUrl ?: R.drawable.ic_launcher_background),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showPhotoBottomSheet = true },
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Edit foto atau avatar",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { showPhotoBottomSheet = true }.padding(8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cuaners Level: Gold \uD83C\uDFC6",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Minimalist Inputs
            MinimalistTextField(
                label = "Nama Lengkap",
                value = name,
                onValueChange = { name = it },
                placeholder = "John Doe"
            )

            Spacer(modifier = Modifier.height(16.dp))

            MinimalistTextField(
                label = "Username",
                value = username,
                onValueChange = { username = it },
                placeholder = "cuaners_sejati"
            )

            Spacer(modifier = Modifier.height(16.dp))

            MinimalistTextField(
                label = "Bio Singkat",
                value = bio,
                onValueChange = { bio = it },
                placeholder = "Sedang menabung untuk masa depan"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Field with Custom Prefix Dropdown
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nomor HP",
                    color = secondaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text("81234567890", color = Color.DarkGray) },
                    prefix = {
                        Box(modifier = Modifier.clickable { expandedCountryCode = true }) {
                            Text(selectedCountryCode, color = primaryText, modifier = Modifier.padding(end = 8.dp))
                            DropdownMenu(
                                expanded = expandedCountryCode,
                                onDismissRequest = { expandedCountryCode = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                countryCodes.forEach { code ->
                                    DropdownMenuItem(
                                        text = { Text(code, color = primaryText) },
                                        onClick = {
                                            selectedCountryCode = code
                                            expandedCountryCode = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = inputBackground,
                        unfocusedContainerColor = inputBackground,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedTextColor = primaryText,
                        unfocusedTextColor = primaryText
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            MinimalistTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholder = "john@example.com"
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // Tanggal Lahir (Read-only input that opens picker)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tanggal Lahir",
                    color = secondaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = dob,
                    onValueChange = { },
                    readOnly = true,
                    placeholder = { Text("DD/MM/YYYY", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                    enabled = false, // Use enabled = false and clickable to intercept click
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = primaryText,
                        disabledBorderColor = borderColor,
                        disabledContainerColor = inputBackground,
                        disabledPlaceholderColor = Color.DarkGray,
                        disabledTrailingIconColor = primaryText
                    ),
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pilih Tanggal")
                    },
                    singleLine = true
                )
            }



            Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom bar
        }
    }
}

@Composable
fun MinimalistTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    prefix: String? = null
) {
    val primaryText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val inputBackground = Color.Transparent
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val focusedBorderColor = MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = secondaryText,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.DarkGray) },
            prefix = { if (prefix != null) Text(prefix, color = primaryText) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBackground,
                unfocusedContainerColor = inputBackground,
                focusedBorderColor = focusedBorderColor,
                unfocusedBorderColor = borderColor,
                focusedTextColor = primaryText,
                unfocusedTextColor = primaryText
            ),
            singleLine = true
        )
    }
}

@Composable
fun BottomSheetMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    textColor: Color = Color.White,
    iconColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = text, tint = iconColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = textColor, fontSize = 16.sp)
    }
}

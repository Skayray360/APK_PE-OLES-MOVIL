package com.example.feature.assistant

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.datastore.LocalUserPreferences
import com.example.core.model.ChatAction
import com.example.core.model.ChatMessage
import com.example.core.model.FavoriteItem
import com.example.core.model.NewsArticle
import com.example.core.model.Order
import com.example.core.model.Product
import com.example.core.model.QuoteRequest
import com.example.core.model.UserProfile
import com.example.feature.common.CategoryBadge
import com.example.feature.common.CorporateTopBar
import com.example.feature.common.formatCurrencyMxn
import com.example.feature.main.AssistantUiState

@Composable
fun AssistantScreen(
    messages: List<ChatMessage>,
    uiState: AssistantUiState,
    cartCount: Int,
    onSendPrompt: (String) -> Unit,
    onNewsCardClick: (NewsArticle) -> Unit,
    onProductCardClick: (Product) -> Unit,
    onActionClick: (ChatAction) -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCartClick: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    val suggestedQuestions = listOf(
        "¿Cuáles son las noticias recientes?",
        "Muéstrame información sobre sostenibilidad.",
        "¿Qué eventos están publicados?",
        "Buscar artículos de la tienda.",
        "¿Cómo puedo contactar a Peñoles?",
        "¿Dónde consulto información corporativa?"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        CorporateTopBar(
            subtitle = "Asistente Peñoles • RAG + Gemini",
            cartCount = cartCount,
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            onCartClick = onCartClick
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 900.dp)
            ) {
                // Tarjetas horizontales de preguntas sugeridas
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("assistant_suggested_prompts_row")
                ) {
                    items(suggestedQuestions) { suggestion ->
                        AssistChip(
                            onClick = { onSendPrompt(suggestion) },
                            label = { Text(suggestion, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("assistant_messages_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatBubbleItem(
                            message = msg,
                            onNewsClick = onNewsCardClick,
                            onProductClick = onProductCardClick,
                            onActionClick = onActionClick
                        )
                    }

                    if (uiState is AssistantUiState.Processing) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Consultando fuentes institucionales verificadas...",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    if (uiState is AssistantUiState.Error) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = uiState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Barra inferior de escritura
        Surface(
            tonalElevation = 3.dp,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 900.dp)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Escribe tu consulta para el Asistente Peñoles...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("assistant_input_field"),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                onSendPrompt(inputText)
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank() && uiState !is AssistantUiState.Processing,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("assistant_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar consulta",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    onNewsClick: (NewsArticle) -> Unit,
    onProductClick: (Product) -> Unit,
    onActionClick: (ChatAction) -> Unit
) {
    val isUser = message.role == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Avatar Asistente Peñoles",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            Surface(
                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                tonalElevation = 2.dp,
                shadowElevation = 1.dp,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                    )

                    if (!isUser && message.citations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Fuentes: ${message.citations.joinToString(" • ")}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (!isUser && message.requiresHumanSupport) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Esta consulta involucra atención comercial especializada o cotización formal.",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tarjetas enriquecidas de noticias recuperadas
        if (message.relatedNews.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            message.relatedNews.forEach { news ->
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 46.dp, top = 4.dp)
                        .clickable { onNewsClick(news) },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        CategoryBadge(text = news.categoryId)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = news.title,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Toca para leer la publicación completa →",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Tarjetas enriquecidas de productos recuperados
        if (message.relatedProducts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            message.relatedProducts.forEach { prod ->
                val isInd = prod.productType == "INDUSTRIAL"
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 46.dp, top = 4.dp)
                        .clickable { onProductClick(prod) },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = prod.name,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = if (isInd) "Catálogo Industrial • Solicitar cotización" else formatCurrencyMxn(prod.price),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Ver →",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Acciones de navegación interna
        if (message.relatedActions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.padding(start = 46.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                message.relatedActions.forEach { action ->
                    AssistChip(
                        onClick = { onActionClick(action) },
                        label = { Text(action.label) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileAndAdminScreen(
    userProfile: UserProfile,
    preferences: LocalUserPreferences,
    favorites: List<FavoriteItem>,
    orders: List<Order>,
    quotes: List<QuoteRequest>,
    cartCount: Int,
    onToggleDarkTheme: (Boolean) -> Unit,
    onToggleAutoCarousel: (Boolean) -> Unit,
    onTogglePushConsent: (Boolean) -> Unit,
    onToggleNotificationTopic: (String) -> Unit,
    onCancelOrder: (String) -> Unit,
    onPublishNewsAdmin: (String, String, String, String, String) -> Unit,
    onPublishProductAdmin: (String, String, String, String, Double, Int, String, String) -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCartClick: () -> Unit,
    onSignOut: () -> Unit
) {
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAdminCmsDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onTogglePushConsent(granted)
    }

    val notificationTopics = listOf(
        "Noticias importantes",
        "Eventos",
        "Sostenibilidad",
        "Promociones autorizadas",
        "Actualización de pedidos"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        CorporateTopBar(
            subtitle = "Perfil, Preferencias y Gestión",
            cartCount = cartCount,
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            onCartClick = onCartClick
        )

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            val isWideTablet = maxWidth >= 700.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 960.dp)
                    .testTag("profile_lazy_column"),
                contentPadding = PaddingValues(if (isWideTablet) 24.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isWideTablet) {
                    // En tablet: Identidad del usuario + Panel CMS lado a lado
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = userProfile.displayName,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Text(
                                            text = userProfile.email,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        CategoryBadge(
                                            text = "Rol: ${userProfile.role}",
                                            isHighlighted = userProfile.role == "ADMINISTRADOR"
                                        )
                                    }
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AdminPanelSettings,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Panel Administrativo y CMS",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Publicación de noticias y catálogo con validación de rol en Firestore.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { showAdminCmsDialog = true },
                                        modifier = Modifier.testTag("open_cms_admin_button")
                                    ) {
                                        Text("Abrir CMS")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // 1. Tarjeta de identidad del usuario y Rol RBAC
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = userProfile.displayName,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = userProfile.email,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    CategoryBadge(
                                        text = "Rol: ${userProfile.role}",
                                        isHighlighted = userProfile.role == "ADMINISTRADOR"
                                    )
                                }
                            }
                        }
                    }

                    // 2. Panel de Administración CMS (Protegido en Firestore por rol)
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Panel Administrativo y CMS",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Publicación de noticias y catálogo con validación de rol en Firestore.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { showAdminCmsDialog = true },
                                    modifier = Modifier.testTag("open_cms_admin_button")
                                ) {
                                    Text("Abrir CMS")
                                }
                            }
                        }
                    }
                }

                // 3. Favoritos y Noticias Guardadas
                item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Favoritos y Noticias Guardadas (${favorites.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (favorites.isEmpty()) {
                            Text(
                                text = "Aún no has guardado noticias o productos en tus favoritos.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            favorites.take(5).forEach { fav ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "• ${fav.title}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    CategoryBadge(text = fav.targetType, isHighlighted = false)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Historial de Pedidos Promocionales (Modo Demostración)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Historial de Pedidos Demostrativos (${orders.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (orders.isEmpty()) {
                            Text(
                                text = "No tienes pedidos registrados.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            orders.forEach { order ->
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Ref: ${order.paymentReference}",
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                            CategoryBadge(text = order.status)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = order.itemsSummary,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "Total validado: ${formatCurrencyMxn(order.total)}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (order.status != "CANCELADO" && order.status != "ENTREGADO") {
                                            TextButton(
                                                onClick = { onCancelOrder(order.id) }
                                            ) {
                                                Text("Cancelar pedido")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Solicitudes de Cotización Industrial
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RequestQuote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cotizaciones Industriales (${quotes.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (quotes.isEmpty()) {
                            Text(
                                text = "No has enviado solicitudes de cotización del catálogo industrial.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            quotes.forEach { quote ->
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = quote.productName,
                                                style = MaterialTheme.typography.titleSmall,
                                                modifier = Modifier.weight(1f)
                                            )
                                            CategoryBadge(text = quote.status, isHighlighted = false)
                                        }
                                        Text(
                                            text = "Empresa: ${quote.companyName} • Volumen: ${quote.estimatedVolume}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. Preferencias Visuales y Consentimiento de Notificaciones
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Preferencias y Notificaciones",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DarkMode, contentDescription = null)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Modo oscuro", style = MaterialTheme.typography.bodyMedium)
                            }
                            Switch(
                                checked = preferences.darkThemeEnabled,
                                onCheckedChange = onToggleDarkTheme,
                                modifier = Modifier.testTag("dark_theme_switch")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Avance automático de carrusel",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Switch(
                                checked = preferences.autoAdvanceCarousel,
                                onCheckedChange = onToggleAutoCarousel
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Notificaciones Push",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Consentimiento explícito conforme a Android 13+",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = preferences.pushNotificationsConsent,
                                onCheckedChange = { enabled ->
                                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        onTogglePushConsent(enabled)
                                    }
                                }
                            )
                        }

                        Text(
                            text = "Categorías de notificaciones suscritas:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            notificationTopics.forEach { topic ->
                                val isSubscribed = topic in preferences.subscribedNotificationTopics
                                FilterChip(
                                    selected = isSubscribed,
                                    onClick = { onToggleNotificationTopic(topic) },
                                    label = { Text(topic) }
                                )
                            }
                        }
                    }
                }
            }

            // 7. Aviso de Privacidad y Cerrar Sesión
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showPrivacyDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Aviso de privacidad")
                    }
                    Button(
                        onClick = onSignOut,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cerrar sesión")
                    }
                }
            }
        }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Aviso de Privacidad e Información Legal") },
            text = {
                Text(
                    "Peñoles Conecta protege tu información personal bajo un modelo de mínimo almacenamiento y aislamiento por usuario en Firestore.\n\n" +
                        "• Autenticación segura mediante Google Sign-In.\n" +
                        "• No se almacenan contraseñas en texto plano ni números de tarjetas bancarias.\n" +
                        "• Esta aplicación opera como entorno demostrativo e informativo inspirado en penoles.com.mx."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }

    if (showAdminCmsDialog) {
        AdminCmsModal(
            userRole = userProfile.role,
            onDismiss = { showAdminCmsDialog = false },
            onPublishNews = { title, summary, content, category, author ->
                onPublishNewsAdmin(title, summary, content, category, author)
                showAdminCmsDialog = false
            },
            onPublishProduct = { name, desc, cat, sku, price, stock, type, specs ->
                onPublishProductAdmin(name, desc, cat, sku, price, stock, type, specs)
                showAdminCmsDialog = false
            }
        )
    }
}

@Composable
private fun AdminCmsModal(
    userRole: String,
    onDismiss: () -> Unit,
    onPublishNews: (String, String, String, String, String) -> Unit,
    onPublishProduct: (String, String, String, String, Double, Int, String, String) -> Unit
) {
    var mode by remember { mutableStateOf("NEWS") } // NEWS or PRODUCT
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Sostenibilidad") }

    var prodName by remember { mutableStateOf("") }
    var prodDesc by remember { mutableStateOf("") }
    var prodSku by remember { mutableStateOf("PEN-CMS-01") }
    var prodPrice by remember { mutableStateOf("390.0") }
    var prodStock by remember { mutableStateOf("25") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("CMS Administrativo (Rol actual: $userRole)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = mode == "NEWS",
                        onClick = { mode = "NEWS" },
                        label = { Text("Nueva Noticia") }
                    )
                    FilterChip(
                        selected = mode == "PRODUCT",
                        onClick = { mode = "PRODUCT" },
                        label = { Text("Nuevo Artículo") }
                    )
                }

                if (mode == "NEWS") {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titular de la noticia") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Resumen corto") },
                        maxLines = 2
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Contenido completo (mín. 10 caracteres)") },
                        minLines = 2
                    )
                } else {
                    OutlinedTextField(
                        value = prodName,
                        onValueChange = { prodName = it },
                        label = { Text("Nombre del producto") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = prodDesc,
                        onValueChange = { prodDesc = it },
                        label = { Text("Descripción") },
                        maxLines = 2
                    )
                    OutlinedTextField(
                        value = prodPrice,
                        onValueChange = { prodPrice = it },
                        label = { Text("Precio MXN") },
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (mode == "NEWS") {
                        onPublishNews(
                            title.ifBlank { "Comunicado Institucional Peñoles" },
                            summary.ifBlank { "Resumen del comunicado publicado desde el CMS." },
                            content.ifBlank { "Contenido detallado del comunicado institucional publicado en Peñoles Conecta." },
                            category,
                            "CMS Comunicación Corporativa"
                        )
                    } else {
                        onPublishProduct(
                            prodName.ifBlank { "Artículo Institucional Conecta" },
                            prodDesc.ifBlank { "Artículo promocional publicado desde el panel administrativo." },
                            "Institucional",
                            prodSku,
                            prodPrice.toDoubleOrNull() ?: 390.0,
                            prodStock.toIntOrNull() ?: 20,
                            "PROMOTIONAL",
                            "Especificación institucional verificada."
                        )
                    }
                }
            ) {
                Text("Publicar en Firestore")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

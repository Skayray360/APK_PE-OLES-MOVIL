package com.example.feature.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import java.text.NumberFormat
import java.util.Locale

/**
 * Información adaptativa de ventana y accesibilidad tipográfica:
 * - Móvil estrecho (< 360.dp): margen 16.dp
 * - Móvil normal (360..599.dp): margen 20.dp
 * - Tablet compacta/media (600..839.dp): margen 24.dp
 * - Tablet expandida (>= 840.dp): margen 32.dp
 */
data class AdaptiveWindowInfo(
    val maxWidth: Dp,
    val maxHeight: Dp,
    val isNarrowMobile: Boolean,
    val isTablet: Boolean,
    val isExpandedTablet: Boolean,
    val horizontalPadding: Dp,
    val contentMaxWidth: Dp,
    val gridColumns: Int,
    val fontScale: Float,
    val isLargeFontScale: Boolean
)

@Composable
fun AdaptiveWindowBox(
    modifier: Modifier = Modifier,
    content: @Composable (AdaptiveWindowInfo) -> Unit
) {
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val isLargeFontScale = fontScale >= 1.3f

    BoxWithConstraints(modifier = modifier) {
        val width = maxWidth
        val isNarrow = width < 360.dp
        val isTablet = width >= 600.dp
        val isExpanded = width >= 840.dp
        val hPadding = when {
            isExpanded -> 32.dp
            isTablet -> 24.dp
            isNarrow -> 16.dp
            else -> 20.dp
        }
        val maxContent = when {
            isExpanded -> 1200.dp
            isTablet -> 920.dp
            else -> width
        }
        val cols = when {
            isExpanded -> if (isLargeFontScale) 2 else 3
            isTablet -> 2
            else -> 1
        }
        content(
            AdaptiveWindowInfo(
                maxWidth = width,
                maxHeight = maxHeight,
                isNarrowMobile = isNarrow,
                isTablet = isTablet,
                isExpandedTablet = isExpanded,
                horizontalPadding = hPadding,
                contentMaxWidth = maxContent,
                gridColumns = cols,
                fontScale = fontScale,
                isLargeFontScale = isLargeFontScale
            )
        )
    }
}

/**
 * Mapa explícito de nombres de archivo objetivo (.webp) para los 5 productos promocionales
 * actuales de la tienda. Mientras los archivos .webp autorizados no existan en `res/drawable`,
 * la capa de presentación resuelve cada ID a su placeholder vectorial neutro e individual.
 */
object PenolesStoreAssetRegistry {
    val targetWebpByProductId: Map<String, String> = mapOf(
        "promo_termo_acero" to "img_store_termo_penoles.webp",
        "promo_gorra_institucional" to "img_store_gorra_penoles.webp",
        "promo_libreta_ejecutiva" to "img_store_libreta_penoles.webp",
        "promo_playera_polo" to "img_store_polo_penoles.webp",
        "promo_mochila_ejecutiva" to "img_store_mochila_penoles.webp"
    )

    val placeholderResByProductId: Map<String, Int> = mapOf(
        "promo_termo_acero" to R.drawable.ic_placeholder_store_termo,
        "promo_gorra_institucional" to R.drawable.ic_placeholder_store_gorra,
        "promo_libreta_ejecutiva" to R.drawable.ic_placeholder_store_libreta,
        "promo_playera_polo" to R.drawable.ic_placeholder_store_polo,
        "promo_mochila_ejecutiva" to R.drawable.ic_placeholder_store_mochila,
        "ind_plata_afinada" to R.drawable.img_industrial_silver_zinc_1793310481982,
        "ind_zinc_shg" to R.drawable.img_industrial_silver_zinc_1793310481982,
        "ind_sulfato_sodio" to R.drawable.img_industrial_silver_zinc_1793310481982,
        "ind_oxido_magnesio" to R.drawable.img_industrial_silver_zinc_1793310481982
    )

    val localEditorialAndIndustrialKeys: Map<String, Int> = mapOf(
        // Claves exactas y alias históricos de banners y noticias
        "img_banner_sustainability" to R.drawable.img_banner_sustainability_1793310480917,
        "img_banner_sustainability_1793310480917" to R.drawable.img_banner_sustainability_1793310480917,
        "img_banner_mining_innovation" to R.drawable.img_banner_mining_innovation_1793310478889,
        "img_banner_mining_innovation_1793310478889" to R.drawable.img_banner_mining_innovation_1793310478889,
        "img_industrial_silver_zinc" to R.drawable.img_industrial_silver_zinc_1793310481982,
        "img_industrial_silver_zinc_1793310481982" to R.drawable.img_industrial_silver_zinc_1793310481982,
        // Claves explícitas por producto (si se almacenan en imageUrl)
        "img_store_termo_penoles" to R.drawable.ic_placeholder_store_termo,
        "img_store_termo_penoles.webp" to R.drawable.ic_placeholder_store_termo,
        "img_store_gorra_penoles" to R.drawable.ic_placeholder_store_gorra,
        "img_store_gorra_penoles.webp" to R.drawable.ic_placeholder_store_gorra,
        "img_store_libreta_penoles" to R.drawable.ic_placeholder_store_libreta,
        "img_store_libreta_penoles.webp" to R.drawable.ic_placeholder_store_libreta,
        "img_store_polo_penoles" to R.drawable.ic_placeholder_store_polo,
        "img_store_polo_penoles.webp" to R.drawable.ic_placeholder_store_polo,
        "img_store_mochila_penoles" to R.drawable.ic_placeholder_store_mochila,
        "img_store_mochila_penoles.webp" to R.drawable.ic_placeholder_store_mochila
    )
}

fun isValidRemoteHttpUrl(raw: String): Boolean {
    val trimmed = raw.trim()
    return trimmed.startsWith("https://", ignoreCase = true) ||
        trimmed.startsWith("http://", ignoreCase = true)
}

@DrawableRes
fun resolveProductFallbackDrawable(productId: String?, imageKeyOrUrl: String): Int {
    val cleanId = productId?.trim().orEmpty()
    if (cleanId.isNotEmpty()) {
        PenolesStoreAssetRegistry.placeholderResByProductId[cleanId]?.let { return it }
    }
    val cleanKey = imageKeyOrUrl.trim()
    PenolesStoreAssetRegistry.localEditorialAndIndustrialKeys[cleanKey]?.let { return it }
    PenolesStoreAssetRegistry.placeholderResByProductId[cleanKey]?.let { return it }
    return R.drawable.ic_placeholder_product_neutral
}

@DrawableRes
fun resolveEditorialOrProductDrawable(
    imageKeyOrUrl: String,
    productId: String? = null,
    isProduct: Boolean = false
): Int {
    val cleanKey = imageKeyOrUrl.trim()
    val cleanProductId = productId?.trim().orEmpty()

    // 1. Si corresponde a un producto con ID registrado y la clave es vacía o el alias legado "img_promo_store_kit"
    if (cleanProductId.isNotEmpty() && (cleanKey.isEmpty() || cleanKey == "img_promo_store_kit" || cleanKey == "img_promo_store_kit_1793310481110")) {
        PenolesStoreAssetRegistry.placeholderResByProductId[cleanProductId]?.let { return it }
        return R.drawable.ic_placeholder_product_neutral
    }

    // 2. Si la clave local coincide exactamente con un alias o recurso conocido
    PenolesStoreAssetRegistry.localEditorialAndIndustrialKeys[cleanKey]?.let { return it }

    // 3. Si el productId está en el mapa explícito
    if (cleanProductId.isNotEmpty()) {
        PenolesStoreAssetRegistry.placeholderResByProductId[cleanProductId]?.let { return it }
    }

    // 4. Si la clave era el antiguo kit genérico ("Global Solutions"), sustituir siempre por placeholder neutro de producto
    if (cleanKey == "img_promo_store_kit" || cleanKey == "img_promo_store_kit_1793310481110") {
        return R.drawable.ic_placeholder_product_neutral
    }

    // 5. Placeholders separados para producto vs noticia/banner
    return if (isProduct || cleanProductId.isNotEmpty()) {
        R.drawable.ic_placeholder_product_neutral
    } else {
        R.drawable.ic_placeholder_news_editorial
    }
}

/**
 * Componente unificado de imagen para Peñoles Conecta.
 * - Da prioridad a URLs HTTP/HTTPS válidas específicas mediante Coil (`AsyncImage`).
 * - Resuelve claves locales mediante un mapa explícito (sin usar `contains("promo")` ni `contains("mining")`).
 * - Retira por completo `img_promo_store_kit_1793310481110.jpg` ("Global Solutions").
 * - Separa placeholders de noticias (`ic_placeholder_news_editorial`) y de productos (`ic_placeholder_product_neutral` / individuales).
 */
@Composable
fun PenolesImage(
    imageKeyOrUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    productId: String? = null,
    isProduct: Boolean = false
) {
    val isPromoProduct = isProduct || (productId != null && productId.startsWith("promo_"))
    val fallbackRes = resolveEditorialOrProductDrawable(
        imageKeyOrUrl = imageKeyOrUrl,
        productId = productId,
        isProduct = isPromoProduct
    )

    if (isValidRemoteHttpUrl(imageKeyOrUrl)) {
        AsyncImage(
            model = imageKeyOrUrl.trim(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            placeholder = painterResource(id = fallbackRes),
            error = painterResource(id = fallbackRes)
        )
    } else {
        Image(
            painter = painterResource(id = fallbackRes),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

/**
 * Identificación institucional textual temporal mientras no exista el archivo oficial autorizado
 * del logotipo corporativo de Industrias Peñoles (`ic_logo_penoles_oficial`).
 * Cumple la directriz de marca: no dibuja una "P" inventada ni crea un símbolo no verificado.
 */
@Composable
fun CorporateBrandIdentityBadge(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = if (compact) 8.dp else 10.dp,
                vertical = if (compact) 4.dp else 6.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(if (compact) 14.dp else 18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "PEÑOLES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CorporateTopBar(
    title: String = "Peñoles Conecta",
    subtitle: String? = null,
    cartCount: Int = 0,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    CorporateBrandIdentityBadge()
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.semantics { heading() }
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("top_bar_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar en Peñoles Conecta",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("top_bar_notifications_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Avisos y notificaciones corporativas",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("top_bar_cart_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text(
                                            text = cartCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = if (cartCount > 0) {
                                    "Carrito promocional, $cartCount artículos"
                                } else {
                                    "Carrito promocional vacío"
                                },
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
fun CategoryBadge(
    text: String,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = true
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isHighlighted) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (isHighlighted) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (isHighlighted) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun DemoStoreNoticeBanner(
    modifier: Modifier = Modifier,
    secondaryText: String? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Aviso de tienda de demostración",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Tienda de demostración · Sin cobros reales",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!secondaryText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = secondaryText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeaderRow(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    horizontalPadding: Dp = 20.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(1f)
                .semantics { heading() }
        )
        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = onAction,
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

fun formatCurrencyMxn(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    return "${format.format(amount)} MXN"
}

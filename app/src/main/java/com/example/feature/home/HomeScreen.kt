package com.example.feature.home

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.core.model.Banner
import com.example.core.model.FavoriteItem
import com.example.core.model.NewsArticle
import com.example.core.model.PenolesReferenceCatalog
import com.example.core.model.Product
import com.example.feature.common.AdaptiveWindowBox
import com.example.feature.common.CategoryBadge
import com.example.feature.common.CorporateTopBar
import com.example.feature.common.PenolesImage
import com.example.feature.common.SectionHeaderRow
import com.example.feature.common.formatCurrencyMxn
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    banners: List<Banner>,
    news: List<NewsArticle>,
    products: List<Product>,
    favorites: List<FavoriteItem>,
    cartCount: Int,
    autoAdvanceCarousel: Boolean,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCartClick: () -> Unit,
    onBannerClick: (Banner) -> Unit,
    onQuickCategoryClick: (String) -> Unit,
    onNewsClick: (NewsArticle) -> Unit,
    onViewAllNewsClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onViewAllStoreClick: () -> Unit,
    onToggleFavorite: (String, String, String, String) -> Unit,
    onAssistantCtaClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CorporateTopBar(
            subtitle = "Comunicación institucional, sostenibilidad y catálogo",
            cartCount = cartCount,
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            onCartClick = onCartClick
        )

        AdaptiveWindowBox(modifier = Modifier.fillMaxSize()) { windowInfo ->
            val hPad = windowInfo.horizontalPadding

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 1200.dp)
                        .testTag("home_lazy_column"),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 1. Banner principal institucional (16:9, radio 24.dp, textos en Compose)
                    if (banners.isNotEmpty()) {
                        item {
                            HeroBannerCarousel(
                                banners = banners,
                                autoAdvance = autoAdvanceCarousel,
                                isTablet = windowInfo.isTablet,
                                horizontalPadding = hPad,
                                onBannerClick = onBannerClick
                            )
                        }
                    }

                    // 2. Accesos rápidos por categoría
                    item {
                        Column {
                            SectionHeaderRow(
                                title = "Categorías rápidas",
                                horizontalPadding = hPad
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = hPad),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.testTag("quick_categories_row")
                            ) {
                                items(PenolesReferenceCatalog.quickHomeCategories) { category ->
                                    QuickCategoryChip(
                                        category = category,
                                        onClick = { onQuickCategoryClick(category) }
                                    )
                                }
                            }
                        }
                    }

                    // 3. Noticias recientes (Adaptativo: multicolumna en tablet, lista vertical en móvil)
                    item {
                        SectionHeaderRow(
                            title = "Noticias recientes",
                            actionText = "Ver todas",
                            onActionClick = onViewAllNewsClick,
                            horizontalPadding = hPad
                        )
                    }

                    if (windowInfo.isTablet) {
                        val cols = if (windowInfo.isExpandedTablet && !windowInfo.isLargeFontScale) 3 else 2
                        val recentNews = news.take(if (cols == 3) 3 else 4)
                        val rows = recentNews.chunked(cols)
                        items(rows) { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = hPad),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                rowItems.forEach { article ->
                                    val isFav = favorites.any { it.targetType == "NEWS" && it.targetId == article.id }
                                    Box(modifier = Modifier.weight(1f)) {
                                        RecentNewsHomeCard(
                                            article = article,
                                            isFavorite = isFav,
                                            horizontalPadding = 0.dp,
                                            onClick = { onNewsClick(article) },
                                            onToggleFavorite = {
                                                onToggleFavorite(article.id, "NEWS", article.title, article.categoryId)
                                            },
                                            onShare = {
                                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(
                                                        Intent.EXTRA_TEXT,
                                                        "${article.title}\n${article.summary}\nFuente: ${article.sourceUrl}"
                                                    )
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "Compartir noticia"))
                                            }
                                        )
                                    }
                                }
                                repeat(cols - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    } else {
                        items(news.take(3), key = { it.id }) { article ->
                            val isFav = favorites.any { it.targetType == "NEWS" && it.targetId == article.id }
                            RecentNewsHomeCard(
                                article = article,
                                isFavorite = isFav,
                                horizontalPadding = hPad,
                                onClick = { onNewsClick(article) },
                                onToggleFavorite = {
                                    onToggleFavorite(article.id, "NEWS", article.title, article.categoryId)
                                },
                                onShare = {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "${article.title}\n${article.summary}\nFuente: ${article.sourceUrl}"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Compartir noticia"))
                                }
                            )
                        }
                    }

                    // 4. Productos destacados ("Explora nuestra tienda")
                    item {
                        Column {
                            SectionHeaderRow(
                                title = "Explora nuestra tienda",
                                actionText = "Ir a tienda",
                                onActionClick = onViewAllStoreClick,
                                horizontalPadding = hPad
                            )
                            Text(
                                text = "Tienda de demostración · Sin cobros reales",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = hPad)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = hPad),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.testTag("home_store_carousel")
                            ) {
                                items(products.take(6), key = { it.id }) { product ->
                                    HomeFeaturedProductCard(
                                        product = product,
                                        onClick = { onProductClick(product) }
                                    )
                                }
                            }
                        }
                    }

                    // 5. Acceso directo al Asistente Peñoles
                    item {
                        AssistantHighlightCard(
                            horizontalPadding = hPad,
                            onClick = onAssistantCtaClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroBannerCarousel(
    banners: List<Banner>,
    autoAdvance: Boolean,
    isTablet: Boolean,
    horizontalPadding: Dp = 20.dp,
    onBannerClick: (Banner) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(pagerState.currentPage, isDragged, autoAdvance, banners.size) {
        if (autoAdvance && !isDragged && banners.size > 1) {
            delay(6000L)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_hero_carousel")
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            pageSpacing = 16.dp
        ) { page ->
            val banner = banners[page]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isTablet) {
                            Modifier.heightIn(min = 260.dp, max = 320.dp)
                        } else {
                            Modifier
                                .aspectRatio(16f / 9f)
                                .heightIn(min = 220.dp)
                        }
                    )
                    .clickable { onBannerClick(banner) }
                    .semantics {
                        contentDescription = "Destacado institucional: ${banner.title}"
                    },
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    PenolesImage(
                        imageKeyOrUrl = banner.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Degradado oscuro localizado para garantizar contraste >= 4.5:1 del texto sobre foto real
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0.0f to Color.Black.copy(alpha = 0.18f),
                                        0.45f to Color.Black.copy(alpha = 0.52f),
                                        1.0f to Color.Black.copy(alpha = 0.88f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = Color(0xFFC41E24),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = banner.category,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "Comunicación Institucional",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = banner.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = banner.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.92f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onBannerClick(banner) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC41E24),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Text(
                                text = "Conoce más",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        if (banners.size > 1) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(banners.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (selected) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = 1.dp,
                                color = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickCategoryChip(
    category: String,
    onClick: () -> Unit
) {
    val icon = when (category) {
        "Innovación" -> Icons.Default.Lightbulb
        "Sostenibilidad" -> Icons.Default.Eco
        "Seguridad" -> Icons.Default.HealthAndSafety
        "Comunidad" -> Icons.Default.Diversity3
        "Eventos" -> Icons.Default.CalendarMonth
        else -> Icons.Default.Newspaper
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.heightIn(min = 48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RecentNewsHomeCard(
    article: NewsArticle,
    isFavorite: Boolean,
    horizontalPadding: Dp = 20.dp,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .clickable { onClick() }
            .testTag("home_news_card_${article.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp)
            ) {
                PenolesImage(
                    imageKeyOrUrl = article.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                CategoryBadge(
                    text = article.categoryId,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${article.readingTimeMinutes} min de lectura",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isFavorite) {
                                    "Quitar ${article.title} de favoritos"
                                } else {
                                    "Guardar ${article.title} en favoritos"
                                },
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = onShare,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir ${article.title}",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeFeaturedProductCard(
    product: Product,
    onClick: () -> Unit
) {
    val isIndustrial = product.productType == "INDUSTRIAL"
    OutlinedCard(
        modifier = Modifier
            .width(256.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                PenolesImage(
                    imageKeyOrUrl = product.imageUrl,
                    productId = product.id,
                    isProduct = !isIndustrial,
                    contentDescription = product.name,
                    contentScale = if (isIndustrial) ContentScale.Crop else ContentScale.Fit,
                    modifier = if (isIndustrial) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    }
                )
                CategoryBadge(
                    text = if (isIndustrial) "Catálogo Industrial" else product.categoryId,
                    isHighlighted = !isIndustrial,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.heightIn(min = 44.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isIndustrial) {
                        "Sujeto a cotización empresarial"
                    } else {
                        "${formatCurrencyMxn(product.price)} · Demo"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isIndustrial) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                FilledTonalButton(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isIndustrial) "Solicitar cotización" else "Ver artículo",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun AssistantHighlightCard(
    horizontalPadding: Dp = 20.dp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .clickable { onClick() }
            .testTag("home_assistant_cta_card"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "¿Buscas información? Pregunta al Asistente Peñoles",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Consulta noticias, sostenibilidad, eventos y fichas técnicas con referencias verificadas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Abrir Asistente Peñoles",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

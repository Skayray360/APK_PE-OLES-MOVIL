package com.example.feature.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

    Column(modifier = Modifier.fillMaxSize()) {
        CorporateTopBar(
            subtitle = "Descubre lo que sucede en Peñoles",
            cartCount = cartCount,
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            onCartClick = onCartClick
        )

        AdaptiveWindowBox(modifier = Modifier.fillMaxSize()) { windowInfo ->
            val hPad = windowInfo.horizontalPadding
            val bannerHeight = if (windowInfo.isTablet) 285.dp else 235.dp

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 1200.dp)
                        .testTag("home_lazy_column"),
                    contentPadding = PaddingValues(bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. Saludo y buscador rápido
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = hPad, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Descubre lo que sucede en Peñoles",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Actualidad institucional, innovación operativa, sostenibilidad y catálogo corporativo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 2. Carrusel principal con avance automático opcional y pausa al interactuar
                    if (banners.isNotEmpty()) {
                        item {
                            HeroBannerCarousel(
                                banners = banners,
                                autoAdvance = autoAdvanceCarousel,
                                bannerHeight = bannerHeight,
                                horizontalPadding = hPad,
                                onBannerClick = onBannerClick
                            )
                        }
                    }

                    // 3. Categorías rápidas horizontales
                    item {
                        Column {
                            SectionHeaderRow(title = "Categorías rápidas")
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = hPad),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
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

                    // 4. Noticias recientes (Adaptativo: multicolumna en tablet, lista vertical en teléfono)
                    item {
                        SectionHeaderRow(
                            title = "Noticias recientes",
                            actionText = "Ver todas",
                            onActionClick = onViewAllNewsClick
                        )
                    }

                    if (windowInfo.isTablet) {
                        val recentNews = news.take(if (windowInfo.isExpandedTablet) 3 else 4)
                        val cols = if (windowInfo.isExpandedTablet) 3 else 2
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

                    // 5. Sección de Tienda ("Explora nuestra tienda")
                    item {
                        Column {
                            SectionHeaderRow(
                                title = "Explora nuestra tienda",
                                actionText = "Ir a tienda",
                                onActionClick = onViewAllStoreClick
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = hPad),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
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

                    // 6. Sección final: Acceso destacado al Asistente Peñoles
                    item {
                        AssistantHighlightCard(
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
    bannerHeight: Dp = 235.dp,
    horizontalPadding: Dp = 16.dp,
    onBannerClick: (Banner) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(pagerState.currentPage, isDragged, autoAdvance, banners.size) {
        if (autoAdvance && !isDragged && banners.size > 1) {
            delay(5000L)
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
            pageSpacing = 12.dp
        ) { page ->
            val banner = banners[page]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bannerHeight)
                    .clickable { onBannerClick(banner) }
                    .semantics {
                        contentDescription = "Destacado: ${banner.title}"
                    },
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    PenolesImage(
                        imageKeyOrUrl = banner.imageUrl,
                        contentDescription = banner.title,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.15f),
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CategoryBadge(text = banner.category)
                            Text(
                                text = "Octubre 2026",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
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
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.88f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onBannerClick(banner) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = "Conoce más",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

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
                        .width(if (selected) 24.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                )
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
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        modifier = Modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = category,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
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
    horizontalPadding: Dp = 16.dp,
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
            ) {
                PenolesImage(
                    imageKeyOrUrl = article.imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier.fillMaxSize()
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
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Tiempo de lectura",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${article.readingTimeMinutes} min de lectura • Oct 2026",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Guardar noticia en favoritos",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onShare) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir noticia",
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
            .width(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                PenolesImage(
                    imageKeyOrUrl = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = if (isIndustrial) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = if (isIndustrial) "Catálogo Industrial" else "Disponible (${product.stock})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(40.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isIndustrial) "Sujeto a cotización empresarial" else formatCurrencyMxn(product.price),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isIndustrial) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                FilledTonalButton(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isIndustrial) "Solicitar cotización" else "Ver detalles",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun AssistantHighlightCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() }
            .testTag("home_assistant_cta_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
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
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Asistente Peñoles",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "¿Buscas información? Pregunta a nuestro asistente.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Consulta noticias, sostenibilidad, eventos y productos con referencias verificadas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Abrir Asistente Peñoles",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

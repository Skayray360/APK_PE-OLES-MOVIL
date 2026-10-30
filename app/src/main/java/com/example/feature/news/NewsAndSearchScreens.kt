package com.example.feature.news

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.model.FavoriteItem
import com.example.core.model.NewsArticle
import com.example.core.model.PenolesReferenceCatalog
import com.example.core.model.Product
import com.example.feature.common.CategoryBadge
import com.example.feature.common.CorporateTopBar
import com.example.feature.common.EmptyStateCard
import com.example.feature.common.PenolesImage
import com.example.feature.common.SectionHeaderRow
import com.example.feature.common.formatCurrencyMxn
import com.example.feature.main.GlobalSearchResults
import java.text.SimpleDateFormat
import java.util.Locale

private fun formatOptionalArticleDate(article: NewsArticle): String? {
    val ts = article.createdAt ?: return null
    return runCatching {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX"))
        formatter.format(ts.toDate())
    }.getOrNull()
}

@Composable
fun NewsScreen(
    articles: List<NewsArticle>,
    selectedCategory: String,
    favorites: List<FavoriteItem>,
    cartCount: Int,
    onSelectCategory: (String) -> Unit,
    onArticleClick: (NewsArticle) -> Unit,
    onToggleFavorite: (NewsArticle) -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCartClick: () -> Unit
) {
    var localFilterQuery by rememberSaveable { mutableStateOf("") }

    val filteredArticles = remember(articles, selectedCategory, localFilterQuery) {
        articles.filter { article ->
            val matchesCat = selectedCategory == "Todas" ||
                article.categoryId.equals(selectedCategory, ignoreCase = true) ||
                article.tags.any { it.equals(selectedCategory, ignoreCase = true) }
            val matchesQuery = localFilterQuery.isBlank() ||
                article.title.contains(localFilterQuery, ignoreCase = true) ||
                article.summary.contains(localFilterQuery, ignoreCase = true) ||
                article.content.contains(localFilterQuery, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CorporateTopBar(
            subtitle = "Sala de Prensa y Comunicados Corporativos",
            cartCount = cartCount,
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            onCartClick = onCartClick
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isNarrow = maxWidth < 360.dp
            val isTabletGrid = maxWidth >= 600.dp
            val isExpandedTablet = maxWidth >= 840.dp
            val hPad = when {
                isExpandedTablet -> 32.dp
                isTabletGrid -> 24.dp
                isNarrow -> 16.dp
                else -> 20.dp
            }

            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = localFilterQuery,
                    onValueChange = { localFilterQuery = it },
                    placeholder = {
                        Text(
                            text = "Filtrar comunicados por título o tema...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Filtrar noticias")
                    },
                    trailingIcon = {
                        if (localFilterQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { localFilterQuery = "" },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpiar filtro")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = hPad, vertical = 12.dp)
                        .testTag("news_search_input")
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = hPad),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("news_categories_filter_row")
                ) {
                    items(PenolesReferenceCatalog.newsCategories) { category ->
                        val isSelected = category.equals(selectedCategory, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectCategory(category) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Categoría seleccionada",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            label = {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outline,
                                selectedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredArticles.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Default.Newspaper,
                        title = "No se encontraron publicaciones",
                        subtitle = "Prueba seleccionando otra categoría editorial o ajustando los términos de búsqueda.",
                        actionLabel = "Ver todas las categorías",
                        onAction = {
                            onSelectCategory("Todas")
                            localFilterQuery = ""
                        }
                    )
                } else if (isTabletGrid) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 320.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("news_articles_list"),
                        contentPadding = PaddingValues(horizontal = hPad, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredArticles, key = { it.id }) { article ->
                            val isFav = favorites.any { it.targetType == "NEWS" && it.targetId == article.id }
                            EditorialNewsCard(
                                article = article,
                                isFavorite = isFav,
                                onClick = { onArticleClick(article) },
                                onToggleFavorite = { onToggleFavorite(article) }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("news_articles_list"),
                        contentPadding = PaddingValues(horizontal = hPad, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredArticles, key = { it.id }) { article ->
                            val isFav = favorites.any { it.targetType == "NEWS" && it.targetId == article.id }
                            EditorialNewsCard(
                                article = article,
                                isFavorite = isFav,
                                onClick = { onArticleClick(article) },
                                onToggleFavorite = { onToggleFavorite(article) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorialNewsCard(
    article: NewsArticle,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val formattedDate = remember(article.createdAt) { formatOptionalArticleDate(article) }
    val metadataLine = buildString {
        append("Lectura estimada: ${article.readingTimeMinutes} min")
        if (!formattedDate.isNullOrBlank()) {
            append(" · ")
            append(formattedDate)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("news_article_card_${article.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
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
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (article.author.isNotBlank()) {
                            Text(
                                text = article.author,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = metadataLine,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewsDetailScreen(
    article: NewsArticle,
    recommendedArticles: List<NewsArticle>,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSelectRecommended: (NewsArticle) -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val formattedDate = remember(article.createdAt) { formatOptionalArticleDate(article) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = article.categoryId,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("news_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a noticias"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isFavorite) "Quitar de favoritos" else "Guardar noticia",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${article.title}\n\n${article.summary}\nConsulta más en: ${article.sourceUrl}"
                                )
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Compartir publicación"))
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir publicación"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 860.dp)
                    .testTag("news_detail_scroll"),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    PenolesImage(
                        imageKeyOrUrl = article.imageUrl,
                        contentDescription = article.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    )
                }

                item {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CategoryBadge(text = article.categoryId)
                            Text(
                                text = buildString {
                                    append("${article.readingTimeMinutes} min de lectura")
                                    if (!formattedDate.isNullOrBlank()) {
                                        append(" · ")
                                        append(formattedDate)
                                    }
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.semantics { heading() }
                        )

                        if (article.author.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Fuente: ${article.author}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = article.summary,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Renderizado estructurado de párrafos y subtítulos con bodyLarge (16/24 sp)
                        article.content.lines().forEach { line ->
                            val trimmed = line.trim()
                            when {
                                trimmed.startsWith("## ") -> {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = trimmed.removePrefix("## "),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.semantics { heading() }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                trimmed.startsWith("### ") -> {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = trimmed.removePrefix("### "),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                trimmed.isNotBlank() -> {
                                    Text(
                                        text = trimmed,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (article.tags.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                article.tags.forEach { tag ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("#$tag", style = MaterialTheme.typography.labelMedium) },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.heightIn(min = 48.dp)
                                    )
                                }
                            }
                        }

                        if (article.sourceUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            runCatching { uriHandler.openUri(article.sourceUrl) }
                                        }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Enlace institucional de referencia",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = article.sourceUrl,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "Abrir sitio oficial",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                if (recommendedArticles.isNotEmpty()) {
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionHeaderRow(title = "Noticias relacionadas")
                    }

                    items(recommendedArticles.take(2), key = { it.id }) { rec ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .clickable { onSelectRecommended(rec) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PenolesImage(
                                    imageKeyOrUrl = rec.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    CategoryBadge(text = rec.categoryId)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = rec.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GlobalSearchScreen(
    query: String,
    results: GlobalSearchResults,
    recentSearches: List<String>,
    onQueryChange: (String) -> Unit,
    onExecuteSearch: (String) -> Unit,
    onClearHistory: () -> Unit,
    onBack: () -> Unit,
    onNewsClick: (NewsArticle) -> Unit,
    onProductClick: (Product) -> Unit,
    onCategoryClick: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val suggestedQueries = listOf(
        "Sostenibilidad",
        "Plata afinada",
        "Termo",
        "Agua tratada",
        "Seguridad",
        "Zinc SHG"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Búsqueda Global",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cerrar buscador"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 860.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("¿Qué estás buscando?") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = { onQueryChange("") },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .testTag("global_search_textfield")
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (query.isBlank()) {
                        item {
                            Text(
                                text = "Sugerencias rápidas",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                suggestedQueries.forEach { suggestion ->
                                    AssistChip(
                                        onClick = {
                                            onQueryChange(suggestion)
                                            onExecuteSearch(suggestion)
                                        },
                                        label = {
                                            Text(
                                                text = suggestion,
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.heightIn(min = 48.dp)
                                    )
                                }
                            }
                        }

                        if (recentSearches.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Búsquedas recientes",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    TextButton(
                                        onClick = onClearHistory,
                                        modifier = Modifier.heightIn(min = 48.dp)
                                    ) {
                                        Text("Limpiar historial")
                                    }
                                }
                            }

                            items(recentSearches) { historyItem ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .clickable {
                                            onQueryChange(historyItem)
                                            onExecuteSearch(historyItem)
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = historyItem,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    } else {
                        val totalCount = results.news.size +
                            results.promotionalProducts.size +
                            results.industrialProducts.size +
                            results.faqs.size +
                            results.matchingCategories.size

                        if (totalCount == 0) {
                            item {
                                EmptyStateCard(
                                    icon = Icons.Default.Search,
                                    title = "Sin resultados para \"$query\"",
                                    subtitle = "Intenta con términos como Sostenibilidad, Plata, Zinc, Termo o Seguridad."
                                )
                            }
                        } else {
                            if (results.matchingCategories.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Categorías (${results.matchingCategories.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        results.matchingCategories.forEach { cat ->
                                            AssistChip(
                                                onClick = {
                                                    onExecuteSearch(query)
                                                    onCategoryClick(cat)
                                                },
                                                label = { Text(cat) },
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.heightIn(min = 48.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (results.news.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Noticias y Comunicados (${results.news.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                items(results.news, key = { "search_news_${it.id}" }) { article ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onExecuteSearch(query)
                                                onNewsClick(article)
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            CategoryBadge(text = article.categoryId)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = article.title,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = article.summary,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }

                            if (results.promotionalProducts.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Artículos Promocionales (${results.promotionalProducts.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                items(results.promotionalProducts, key = { "search_promo_${it.id}" }) { prod ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onExecuteSearch(query)
                                                onProductClick(prod)
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                PenolesImage(
                                                    imageKeyOrUrl = prod.imageUrl,
                                                    productId = prod.id,
                                                    isProduct = true,
                                                    contentDescription = prod.name,
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(6.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = prod.name,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Text(
                                                    text = "${prod.categoryId} · Tienda de demostración",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = formatCurrencyMxn(prod.price),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }

                            if (results.industrialProducts.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Catálogo Industrial (${results.industrialProducts.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                items(results.industrialProducts, key = { "search_ind_${it.id}" }) { prod ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onExecuteSearch(query)
                                                onProductClick(prod)
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = prod.name,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = prod.technicalSpecs,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }

                            if (results.faqs.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Información Corporativa (${results.faqs.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                items(results.faqs, key = { "search_faq_${it.id}" }) { faq ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = faq.question,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = faq.answer,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

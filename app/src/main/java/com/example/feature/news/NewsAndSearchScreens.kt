package com.example.feature.news

import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
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
    var localFilterQuery by remember { mutableStateOf("") }

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

    Column(modifier = Modifier.fillMaxSize()) {
        CorporateTopBar(
            subtitle = "Sala de Prensa y Comunicados Corporativos",
            cartCount = cartCount,
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            onCartClick = onCartClick
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isTabletGrid = maxWidth >= 600.dp
            val hPad = if (isTabletGrid) 24.dp else 16.dp

            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = localFilterQuery,
                    onValueChange = { localFilterQuery = it },
                    placeholder = { Text("Filtrar noticias por título, contenido o palabra clave...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Filtrar noticias")
                    },
                    trailingIcon = {
                        if (localFilterQuery.isNotEmpty()) {
                            IconButton(onClick = { localFilterQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpiar filtro")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = hPad, vertical = 10.dp)
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
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredArticles.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Default.Newspaper,
                        title = "No se encontraron publicaciones",
                        subtitle = "Prueba seleccionando otra categoría editorial o ajustando los términos de búsqueda."
                    )
                } else if (isTabletGrid) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("news_articles_list"),
                        contentPadding = PaddingValues(horizontal = hPad, vertical = 8.dp),
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
                        contentPadding = PaddingValues(horizontal = hPad, vertical = 8.dp),
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("news_article_card_${article.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
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
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
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
                        Text(
                            text = article.author,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Lectura estimada: ${article.readingTimeMinutes} min • Octubre 2026",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Guardar en favoritos",
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
                        modifier = Modifier.testTag("news_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a noticias"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Guardar noticia",
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
                        }
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
                                text = "${article.readingTimeMinutes} min de lectura • Octubre 2026",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Fuente: ${article.author}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = article.summary,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Renderizado estructurado de párrafos y subtítulos
                        article.content.lines().forEach { line ->
                            val trimmed = line.trim()
                            when {
                                trimmed.startsWith("## ") -> {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = trimmed.removePrefix("## "),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                                trimmed.startsWith("### ") -> {
                                    Spacer(modifier = Modifier.height(10.dp))
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
                                        label = { Text("#$tag") }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        runCatching { uriHandler.openUri(article.sourceUrl) }
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Enlace institucional de referencia",
                                        style = MaterialTheme.typography.labelLarge
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

                if (recommendedArticles.isNotEmpty()) {
                    item {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        SectionHeaderRow(title = "Noticias recomendadas")
                    }

                    items(recommendedArticles.take(2), key = { it.id }) { rec ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .clickable { onSelectRecommended(rec) },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PenolesImage(
                                    imageKeyOrUrl = rec.imageUrl,
                                    contentDescription = rec.title,
                                    modifier = Modifier.size(72.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    CategoryBadge(text = rec.categoryId)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = rec.title,
                                        style = MaterialTheme.typography.titleSmall,
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
                title = { Text("Búsqueda Global") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(innerPadding),
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
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("global_search_textfield")
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
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
                                    label = { Text(suggestion) }
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
                                TextButton(onClick = onClearHistory) {
                                    Text("Limpiar historial")
                                }
                            }
                        }

                        items(recentSearches) { historyItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                Spacer(modifier = Modifier.height(6.dp))
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    results.matchingCategories.forEach { cat ->
                                        AssistChip(
                                            onClick = {
                                                onExecuteSearch(query)
                                                onCategoryClick(cat)
                                            },
                                            label = { Text(cat) }
                                        )
                                    }
                                }
                            }
                        }

                        if (results.news.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Noticias y Eventos (${results.news.size})",
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
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        CategoryBadge(text = article.categoryId)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = article.title,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = article.summary,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2
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
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = prod.name,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                            Text(
                                                text = prod.categoryId,
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
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = prod.name,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = prod.technicalSpecs,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }

                        if (results.faqs.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Información Corporativa y Preguntas Frecuentes (${results.faqs.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(results.faqs, key = { "search_faq_${it.id}" }) { faq ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = faq.question,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = faq.answer,
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
            }
        }
    }
}

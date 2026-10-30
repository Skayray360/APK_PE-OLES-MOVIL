package com.example.feature.store

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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.model.CartItem
import com.example.core.model.FavoriteItem
import com.example.core.model.Product
import com.example.feature.common.CategoryBadge
import com.example.feature.common.CorporateTopBar
import com.example.feature.common.DemoStoreNoticeBanner
import com.example.feature.common.EmptyStateCard
import com.example.feature.common.PenolesImage
import com.example.feature.common.formatCurrencyMxn
import com.example.ui.theme.PenolesThemeExtras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    products: List<Product>,
    favorites: List<FavoriteItem>,
    cartCount: Int,
    onProductClick: (Product) -> Unit,
    onToggleFavorite: (Product) -> Unit,
    onOpenCart: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    var selectedModalityTab by rememberSaveable { mutableIntStateOf(0) } // 0 = Promocionales, 1 = Catálogo Industrial
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("Todas") }

    val modalityType = if (selectedModalityTab == 0) "PROMOTIONAL" else "INDUSTRIAL"
    val availableCategories = remember(products, modalityType) {
        listOf("Todas") + products
            .filter { it.productType == modalityType }
            .map { it.categoryId }
            .distinct()
    }

    val filteredProducts = remember(products, modalityType, selectedCategory, searchQuery) {
        products.filter { prod ->
            prod.productType == modalityType &&
                (selectedCategory == "Todas" || prod.categoryId.equals(selectedCategory, ignoreCase = true)) &&
                (searchQuery.isBlank() ||
                    prod.name.contains(searchQuery, ignoreCase = true) ||
                    prod.description.contains(searchQuery, ignoreCase = true) ||
                    prod.technicalSpecs.contains(searchQuery, ignoreCase = true))
        }
    }

    val configuration = LocalConfiguration.current
    val isLargeFont = configuration.fontScale >= 1.3f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CorporateTopBar(
            subtitle = "Tienda Promocional y Catálogo Industrial",
            cartCount = cartCount,
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            onCartClick = onOpenCart
        )

        SecondaryTabRow(
            selectedTabIndex = selectedModalityTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("store_modality_tabs")
        ) {
            Tab(
                selected = selectedModalityTab == 0,
                onClick = {
                    selectedModalityTab = 0
                    selectedCategory = "Todas"
                },
                text = {
                    Text(
                        text = "Artículos Promocionales",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                icon = {
                    Icon(
                        imageVector = if (selectedModalityTab == 0) Icons.Default.Check else Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("tab_promotional_store")
            )
            Tab(
                selected = selectedModalityTab == 1,
                onClick = {
                    selectedModalityTab = 1
                    selectedCategory = "Todas"
                },
                text = {
                    Text(
                        text = "Catálogo Industrial",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                icon = {
                    Icon(
                        imageVector = if (selectedModalityTab == 1) Icons.Default.Check else Icons.Default.Factory,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("tab_industrial_catalog")
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isNarrow = maxWidth < 360.dp
            val isTablet = maxWidth >= 600.dp
            val isExpandedTablet = maxWidth >= 840.dp
            val hPad = when {
                isExpandedTablet -> 32.dp
                isTablet -> 24.dp
                isNarrow -> 16.dp
                else -> 20.dp
            }

            // 1 columna en pantallas estrechas (< 360.dp) o con fuente ampliada (>= 1.3x);
            // 2 columnas en móvil normal; adaptable en tablet.
            val gridColumns = when {
                isTablet -> GridCells.Adaptive(minSize = if (isLargeFont) 260.dp else 220.dp)
                isNarrow || isLargeFont -> GridCells.Fixed(1)
                else -> GridCells.Fixed(2)
            }

            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (selectedModalityTab == 0) {
                                "Buscar termos, gorras, libretas..."
                            } else {
                                "Buscar Plata, Zinc SHG, Sulfato de Sodio..."
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar productos"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar búsqueda"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = hPad, vertical = 12.dp)
                        .testTag("store_search_input")
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = hPad),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableCategories) { category ->
                        val isSelected = category.equals(selectedCategory, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Seleccionado",
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

                if (products.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Default.Inventory2,
                        title = "Cargando catálogo institucional",
                        subtitle = "Sincronizando artículos promocionales y fichas técnicas industriales."
                    )
                } else if (filteredProducts.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Default.ShoppingBag,
                        title = "No se encontraron artículos",
                        subtitle = "Ajusta la categoría seleccionada o el término de búsqueda.",
                        actionLabel = "Restablecer filtros",
                        onAction = {
                            selectedCategory = "Todas"
                            searchQuery = ""
                        }
                    )
                } else {
                    LazyVerticalGrid(
                        columns = gridColumns,
                        contentPadding = PaddingValues(
                            start = hPad,
                            end = hPad,
                            top = 8.dp,
                            bottom = 28.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("store_products_grid")
                    ) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            if (selectedModalityTab == 0) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        DemoStoreNoticeBanner(
                                            secondaryText = "Precios y existencias exclusivamente demostrativos para pruebas de flujo."
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "Colección Institucional Peñoles Conecta",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "Artículos promocionales con identidad individual.",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            OutlinedButton(
                                                onClick = onOpenCart,
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .heightIn(min = 48.dp)
                                                    .testTag("store_open_cart_banner_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ShoppingCart,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Carrito ($cartCount)",
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Factory,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Catálogo Industrial Informativo · Sin venta directa",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Text(
                                                text = "Consulta fichas técnicas de metales afinados y químicos industriales con canal de cotización empresarial.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        items(filteredProducts, key = { it.id }) { product ->
                            val isFav = favorites.any { it.targetType == "PRODUCT" && it.targetId == product.id }
                            StoreGridProductCard(
                                product = product,
                                isFavorite = isFav,
                                onClick = { onProductClick(product) },
                                onToggleFavorite = { onToggleFavorite(product) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreGridProductCard(
    product: Product,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val isIndustrial = product.productType == "INDUSTRIAL"
    val isOutOfStock = !isIndustrial && product.stock <= 0
    val semanticColors = PenolesThemeExtras.semanticColors

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .semantics {
                contentDescription = "${product.name}, categoría ${product.categoryId}"
            }
            .testTag("store_product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Contenedor de imagen de producto: 1:1 cuadrado con fondo neutro y ContentScale.Fit para promocionales
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isIndustrial) {
                            Modifier.height(156.dp)
                        } else {
                            Modifier.aspectRatio(1f)
                        }
                    )
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
                            .padding(16.dp)
                    }
                )

                CategoryBadge(
                    text = product.categoryId,
                    isHighlighted = !isIndustrial,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                )

                // Botón de favorito con superficie propia y área táctil mínima de 48x48 dp
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isFavorite) {
                                "Quitar ${product.name} de favoritos"
                            } else {
                                "Guardar ${product.name} en favoritos"
                            },
                            tint = if (isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isIndustrial) {
                    Text(
                        text = "Sujeto a cotización",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ficha técnica informativa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = formatCurrencyMxn(product.price),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Precio demostrativo · Sin cobro real",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isOutOfStock) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isOutOfStock) MaterialTheme.colorScheme.error else semanticColors.success,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOutOfStock) {
                                "Agotado en demostración"
                            } else {
                                "Disponible (${product.stock} pzas demo)"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isOutOfStock) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onClick,
                    enabled = !isOutOfStock,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isIndustrial) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        contentColor = if (isIndustrial) {
                            MaterialTheme.colorScheme.onSecondary
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = when {
                            isIndustrial -> "Solicitar cotización"
                            isOutOfStock -> "Sin existencias"
                            else -> "Ver detalles"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    isFavorite: Boolean,
    userEmail: String,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: (String, Int) -> Unit,
    onOpenCart: () -> Unit,
    onSubmitQuote: (String, String, String, String, String) -> Unit
) {
    BackHandler(onBack = onBack)
    val isIndustrial = product.productType == "INDUSTRIAL"
    val isOutOfStock = !isIndustrial && product.stock <= 0
    var selectedVariant by remember(product) {
        mutableStateOf(product.variants.firstOrNull() ?: "Estándar")
    }
    var quantity by remember { mutableIntStateOf(1) }
    var showQuoteModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isIndustrial) "Ficha Técnica Industrial" else "Detalle de Artículo Promocional",
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
                            .testTag("product_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a tienda"
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
                            contentDescription = if (isFavorite) "Quitar de favoritos" else "Guardar en favoritos",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (!isIndustrial) {
                        IconButton(
                            onClick = onOpenCart,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Abrir carrito promocional"
                            )
                        }
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
                    .widthIn(max = 860.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
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
                                    .padding(24.dp)
                            }
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.padding(20.dp)) {
                        if (!isIndustrial) {
                            DemoStoreNoticeBanner()
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryBadge(
                                text = product.categoryId,
                                isHighlighted = !isIndustrial
                            )
                            Text(
                                text = "SKU: ${product.sku}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nombre completo sin truncar en la vista de detalle
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isIndustrial) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Factory,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Producto industrial regulado sujeto a evaluación técnica y cotización comercial. No disponible para compra directa en carrito.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = formatCurrencyMxn(product.price),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Precio demostrativo · Disponibilidad en almacén de prueba: ${product.stock} unidades",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Especificaciones y Ficha Técnica",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = product.technicalSpecs,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        if (product.variants.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = if (isIndustrial) "Presentaciones industriales" else "Variantes disponibles",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                product.variants.forEach { variant ->
                                    val isSelected = selectedVariant == variant
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedVariant = variant },
                                        leadingIcon = if (isSelected) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Variante seleccionada",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        label = {
                                            Text(
                                                text = variant,
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        modifier = Modifier.heightIn(min = 48.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (isIndustrial) {
                            Button(
                                onClick = { showQuoteModal = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 52.dp)
                                    .testTag("request_industrial_quote_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.RequestQuote, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Solicitar cotización empresarial",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        } else {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Cantidad",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { if (quantity > 1) quantity-- },
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Disminuir cantidad")
                                        }
                                        Text(
                                            text = quantity.toString(),
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                        IconButton(
                                            onClick = { if (quantity < product.stock.coerceAtLeast(1)) quantity++ },
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Aumentar cantidad")
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { onAddToCart(selectedVariant, quantity) },
                                enabled = !isOutOfStock,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 52.dp)
                                    .testTag("add_to_cart_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Agregar al carrito · ${formatCurrencyMxn(product.price * quantity)}",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showQuoteModal) {
        IndustrialQuoteDialog(
            productName = product.name,
            defaultEmail = userEmail,
            onDismiss = { showQuoteModal = false },
            onSubmit = { company, email, phone, volume, notes ->
                onSubmitQuote(company, email, phone, volume, notes)
                showQuoteModal = false
            }
        )
    }
}

@Composable
private fun IndustrialQuoteDialog(
    productName: String,
    defaultEmail: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String) -> Unit
) {
    var companyName by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf(defaultEmail) }
    var contactPhone by remember { mutableStateOf("") }
    var estimatedVolume by remember { mutableStateOf("") }
    var applicationNotes by remember { mutableStateOf("") }

    val isValid = companyName.trim().length >= 2 &&
        contactEmail.trim().length >= 3 &&
        contactPhone.trim().length >= 7 &&
        estimatedVolume.trim().isNotEmpty() &&
        applicationNotes.trim().length >= 3

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Solicitud de Cotización Industrial",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Producto: $productName",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Empresa o Razón Social *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contactEmail,
                    onValueChange = { contactEmail = it },
                    label = { Text("Correo empresarial *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contactPhone,
                    onValueChange = { contactPhone = it },
                    label = { Text("Teléfono de contacto (mín. 7 dígitos) *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = estimatedVolume,
                    onValueChange = { estimatedVolume = it },
                    label = { Text("Volumen / Tonelaje estimado *") },
                    placeholder = { Text("Ej. 20 Toneladas métricas mensuales") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = applicationNotes,
                    onValueChange = { applicationNotes = it },
                    label = { Text("Aplicación industrial y comentarios *") },
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        companyName,
                        contactEmail,
                        contactPhone,
                        estimatedVolume,
                        applicationNotes
                    )
                },
                enabled = isValid,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Enviar solicitud")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartAndCheckoutScreen(
    cartItems: List<CartItem>,
    onBack: () -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onPlaceDemoOrder: (String, String, Double, (String) -> Unit) -> Unit
) {
    BackHandler(onBack = onBack)

    var checkoutStep by rememberSaveable { mutableIntStateOf(1) } // 1: Carrito, 2: Entrega y Envío, 3: Resumen y Checkout Demo, 4: Confirmación
    var shippingAddress by rememberSaveable {
        mutableStateOf("Av. Metalúrgica 550, Col. Industrial, Torreón, Coahuila, C.P. 27000")
    }
    var selectedShippingMethod by rememberSaveable { mutableStateOf("Envío Estándar Nacional (3-5 días)") }
    var confirmedOrderId by rememberSaveable { mutableStateOf<String?>(null) }

    val subtotal = remember(cartItems) {
        cartItems.sumOf { it.unitPriceSnapshot * it.quantity }
    }
    val shippingCost = remember(selectedShippingMethod, cartItems) {
        if (cartItems.isEmpty()) 0.0
        else if (selectedShippingMethod.contains("Exprés")) 220.0
        else 120.0
    }
    val total = subtotal + shippingCost

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (checkoutStep) {
                            1 -> "1. Revisión del Carrito"
                            2 -> "2. Entrega y Selección de Envío"
                            3 -> "3. Resumen de Pedido Demostrativo"
                            else -> "Confirmación de Pedido"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (checkoutStep > 1 && checkoutStep < 4) {
                                checkoutStep--
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
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
                    .widthIn(max = 880.dp)
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    DemoStoreNoticeBanner(
                        secondaryText = "No se solicitan tarjetas bancarias ni se ejecutan cobros reales."
                    )
                }

                when (checkoutStep) {
                    1 -> {
                        if (cartItems.isEmpty()) {
                            EmptyStateCard(
                                icon = Icons.Default.ShoppingCart,
                                title = "Tu carrito promocional está vacío",
                                subtitle = "Explora la tienda de artículos promocionales para agregar termos, gorras, libretas, playeras o mochilas."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(cartItems, key = { it.id }) { item ->
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                        modifier = Modifier.fillMaxWidth()
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
                                                    .size(64.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                PenolesImage(
                                                    imageKeyOrUrl = "",
                                                    productId = item.productId,
                                                    isProduct = true,
                                                    contentDescription = item.productName,
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(8.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = item.productName,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "Variante: ${item.variant}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = formatCurrencyMxn(item.unitPriceSnapshot * item.quantity),
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { onUpdateQuantity(item.id, item.quantity - 1) },
                                                    modifier = Modifier.size(48.dp)
                                                ) {
                                                    Icon(Icons.Default.Remove, contentDescription = "Disminuir cantidad")
                                                }
                                                Text(
                                                    text = item.quantity.toString(),
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                IconButton(
                                                    onClick = { onUpdateQuantity(item.id, item.quantity + 1) },
                                                    modifier = Modifier.size(48.dp)
                                                ) {
                                                    Icon(Icons.Default.Add, contentDescription = "Aumentar cantidad")
                                                }
                                                IconButton(
                                                    onClick = { onRemoveItem(item.id) },
                                                    modifier = Modifier.size(48.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Eliminar del carrito",
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            CartTotalsFooter(
                                subtotal = subtotal,
                                shipping = shippingCost,
                                total = total,
                                buttonText = "Continuar a datos de entrega",
                                onContinue = { checkoutStep = 2 }
                            )
                        }
                    }

                    2 -> {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Domicilio de entrega",
                                style = MaterialTheme.typography.titleLarge
                            )
                            OutlinedTextField(
                                value = shippingAddress,
                                onValueChange = { shippingAddress = it },
                                label = { Text("Dirección de entrega demostrativa") },
                                minLines = 2,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Modalidad de envío",
                                style = MaterialTheme.typography.titleLarge
                            )
                            listOf(
                                "Envío Estándar Nacional (3-5 días)" to 120.0,
                                "Envío Exprés Corporativo (24-48 horas)" to 220.0
                            ).forEach { (methodName, cost) ->
                                val isSelected = selectedShippingMethod == methodName
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedShippingMethod = methodName },
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = if (isSelected) {
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                        } else {
                                            MaterialTheme.colorScheme.surface
                                        }
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { selectedShippingMethod = methodName }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = methodName,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                        Text(
                                            text = formatCurrencyMxn(cost),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        CartTotalsFooter(
                            subtotal = subtotal,
                            shipping = shippingCost,
                            total = total,
                            buttonText = "Revisar resumen del pedido",
                            onContinue = {
                                if (shippingAddress.trim().length >= 5) {
                                    checkoutStep = 3
                                }
                            }
                        )
                    }

                    3 -> {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Resumen del Pedido (Modo Demostración)",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Artículos: ${cartItems.sumOf { it.quantity }} pzas",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Entrega en: $shippingAddress",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Método de envío: $selectedShippingMethod",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                    Text(
                                        text = "Tienda de demostración · Sin cobros reales ni captura de tarjetas bancarias.",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        CartTotalsFooter(
                            subtotal = subtotal,
                            shipping = shippingCost,
                            total = total,
                            buttonText = "Confirmar pedido en modo demostración",
                            onContinue = {
                                onPlaceDemoOrder(shippingAddress, selectedShippingMethod, shippingCost) { orderId ->
                                    confirmedOrderId = orderId
                                    checkoutStep = 4
                                }
                            }
                        )
                    }

                    4 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Confirmado",
                                tint = PenolesThemeExtras.semanticColors.success,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Pedido Demostrativo Registrado",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Folio: ${confirmedOrderId ?: "DEMO"}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Este registro corresponde a una orden de demostración sin cobro real. Puedes consultar su estado en Perfil > Historial de pedidos.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onBack,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 52.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Volver a la tienda",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartTotalsFooter(
    subtotal: Double,
    shipping: Double,
    total: Double,
    buttonText: String,
    onContinue: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal demostrativo:", style = MaterialTheme.typography.bodyMedium)
                Text(formatCurrencyMxn(subtotal), style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Envío estimado:", style = MaterialTheme.typography.bodyMedium)
                Text(formatCurrencyMxn(shipping), style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total (Demostración):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatCurrencyMxn(total),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("checkout_continue_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

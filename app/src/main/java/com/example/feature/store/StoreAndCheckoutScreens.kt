package com.example.feature.store

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.model.CartItem
import com.example.core.model.FavoriteItem
import com.example.core.model.Product
import com.example.feature.common.CategoryBadge
import com.example.feature.common.CorporateTopBar
import com.example.feature.common.EmptyStateCard
import com.example.feature.common.PenolesImage
import com.example.feature.common.formatCurrencyMxn

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
    var selectedModalityTab by remember { mutableIntStateOf(0) } // 0 = Promocionales, 1 = Catálogo Industrial
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas") }

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

    Column(modifier = Modifier.fillMaxSize()) {
        CorporateTopBar(
            subtitle = "Tienda Promocional y Catálogo Industrial",
            cartCount = cartCount,
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            onCartClick = onOpenCart
        )

        SecondaryTabRow(
            selectedTabIndex = selectedModalityTab,
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
                text = { Text("Artículos Promocionales") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.testTag("tab_promotional_store")
            )
            Tab(
                selected = selectedModalityTab == 1,
                onClick = {
                    selectedModalityTab = 1
                    selectedCategory = "Todas"
                },
                text = { Text("Catálogo Industrial") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Factory,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.testTag("tab_industrial_catalog")
            )
        }

        // Aviso de modalidad comercial
        Surface(
            color = if (selectedModalityTab == 0) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedModalityTab == 0) {
                        "Catálogo demostrativo de artículos institucionales. Checkout en modo de demostración."
                    } else {
                        "Catálogo informativo de metales afinados y químicos industriales. Exclusivo para consulta técnica y solicitud de cotización."
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isTablet = maxWidth >= 600.dp
            val hPad = if (isTablet) 24.dp else 16.dp
            val cardMinWidth = if (isTablet) 220.dp else 165.dp

            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            if (selectedModalityTab == 0) "Buscar termos, gorras, libretas..."
                            else "Buscar Plata, Zinc SHG, Sulfato de Sodio..."
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = hPad, vertical = 8.dp)
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
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredProducts.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Default.ShoppingBag,
                        title = "No se encontraron productos",
                        subtitle = "Ajusta la categoría o el término de búsqueda."
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = cardMinWidth),
                        contentPadding = PaddingValues(horizontal = hPad, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("store_products_grid")
                    ) {
                if (selectedModalityTab == 0) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
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
                                        text = "Colección Institucional Conecta",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Artículos promocionales autorizados sujetos a disponibilidad en inventario.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                OutlinedButton(
                                    onClick = onOpenCart,
                                    modifier = Modifier.testTag("store_open_cart_banner_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Carrito ($cartCount)")
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
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("store_product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
            ) {
                PenolesImage(
                    imageKeyOrUrl = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Guardar producto",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.White
                    )
                }
                CategoryBadge(
                    text = product.categoryId,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(40.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isIndustrial) {
                    Text(
                        text = "Sujeto a cotización",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ficha técnica verificada",
                        style = MaterialTheme.typography.labelSmall,
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
                        text = "Disponibles: ${product.stock} pzas",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
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
                        text = if (isIndustrial) "Ficha Técnica Industrial" else "Detalle de Artículo",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("product_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a tienda"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (!isIndustrial) {
                        IconButton(onClick = onOpenCart) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Abrir carrito"
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
                    PenolesImage(
                        imageKeyOrUrl = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    )
                }

                item {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryBadge(text = product.categoryId)
                            Text(
                                text = "SKU: ${product.sku}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isIndustrial) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Factory,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Producto industrial regulado sujeto a evaluación técnica y cotización comercial. No disponible para compra directa en carrito.",
                                        style = MaterialTheme.typography.bodySmall
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
                            Text(
                                text = "Disponibilidad en almacén demostrativo: ${product.stock} unidades",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Especificaciones y Ficha Técnica",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = product.technicalSpecs,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        if (product.variants.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = if (isIndustrial) "Presentaciones industriales" else "Variantes / Tallas disponibles",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                product.variants.forEach { variant ->
                                    FilterChip(
                                        selected = selectedVariant == variant,
                                        onClick = { selectedVariant = variant },
                                        label = { Text(variant) }
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
                                    .height(54.dp)
                                    .testTag("request_industrial_quote_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(imageVector = Icons.Default.RequestQuote, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Solicitar cotización empresarial",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Cantidad:",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (quantity > 1) quantity-- }
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                                    }
                                    Text(
                                        text = quantity.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    IconButton(
                                        onClick = { if (quantity < product.stock.coerceAtLeast(1)) quantity++ }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Aumentar")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { onAddToCart(selectedVariant, quantity) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("add_to_cart_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Agregar al carrito • ${formatCurrencyMxn(product.price * quantity)}",
                                    style = MaterialTheme.typography.titleMedium
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contactEmail,
                    onValueChange = { contactEmail = it },
                    label = { Text("Correo empresarial *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contactPhone,
                    onValueChange = { contactPhone = it },
                    label = { Text("Teléfono de contacto (mín. 7 dígitos) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = estimatedVolume,
                    onValueChange = { estimatedVolume = it },
                    label = { Text("Volumen / Tonelaje estimado *") },
                    placeholder = { Text("Ej. 20 Toneladas métricas mensuales") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = applicationNotes,
                    onValueChange = { applicationNotes = it },
                    label = { Text("Aplicación industrial y comentarios *") },
                    minLines = 2,
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
                enabled = isValid
            ) {
                Text("Enviar solicitud")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
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

    var checkoutStep by remember { mutableIntStateOf(1) } // 1: Carrito, 2: Entrega y Envío, 3: Resumen y Checkout Demo, 4: Confirmación
    var shippingAddress by remember {
        mutableStateOf("Av. Metalúrgica 550, Col. Industrial, Torreón, Coahuila, C.P. 27000")
    }
    var selectedShippingMethod by remember { mutableStateOf("Envío Estándar Nacional (3-5 días)") }
    var confirmedOrderId by remember { mutableStateOf<String?>(null) }

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
                        when (checkoutStep) {
                            1 -> "1. Revisión del Carrito"
                            2 -> "2. Entrega y Selección de Envío"
                            3 -> "3. Resumen y Checkout Demostrativo"
                            else -> "Confirmación y Seguimiento"
                        }
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
                        }
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
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 880.dp)
            ) {
                // Cintillo de modo demostración
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "MODO DEMOSTRACIÓN ACTIVO: No se procesan tarjetas bancarias ni cobros reales. Los totales son validados en Firestore.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                when (checkoutStep) {
                    1 -> {
                        if (cartItems.isEmpty()) {
                            EmptyStateCard(
                                icon = Icons.Default.ShoppingCart,
                                title = "Tu carrito promocional está vacío",
                                subtitle = "Explora la tienda de artículos promocionales para agregar termos, gorras o libretas."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(cartItems, key = { it.id }) { item ->
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
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
                                                    text = item.productName,
                                                    style = MaterialTheme.typography.titleSmall
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
                                                    onClick = { onUpdateQuantity(item.id, item.quantity - 1) }
                                                ) {
                                                    Icon(Icons.Default.Remove, contentDescription = "Menos")
                                                }
                                                Text(
                                                    text = item.quantity.toString(),
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                IconButton(
                                                    onClick = { onUpdateQuantity(item.id, item.quantity + 1) }
                                                ) {
                                                    Icon(Icons.Default.Add, contentDescription = "Más")
                                                }
                                                IconButton(
                                                    onClick = { onRemoveItem(item.id) }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Eliminar",
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
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Domicilio de entrega",
                                style = MaterialTheme.typography.titleMedium
                            )
                            OutlinedTextField(
                                value = shippingAddress,
                                onValueChange = { shippingAddress = it },
                                label = { Text("Dirección completa en México") },
                                minLines = 2,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Modalidad de envío",
                                style = MaterialTheme.typography.titleMedium
                            )
                            listOf(
                                "Envío Estándar Nacional (3-5 días)" to 120.0,
                                "Envío Exprés Corporativo (24-48 horas)" to 220.0
                            ).forEach { (methodName, cost) ->
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedShippingMethod = methodName }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(
                                                selected = selectedShippingMethod == methodName,
                                                onClick = { selectedShippingMethod = methodName }
                                            )
                                            Text(
                                                text = methodName,
                                                style = MaterialTheme.typography.bodyMedium
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
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Resumen del Pedido (Modo Demostración)",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Artículos: ${cartItems.sumOf { it.quantity }} pzas",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Entrega en: $shippingAddress",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Método de envío: $selectedShippingMethod",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                                    Text(
                                        text = "Pasarela de pago: Checkout Seguro Simulado (Sin cargos reales ni captura de tarjetas).",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Pedido Demostrativo Registrado",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Folio: ${confirmedOrderId ?: "DEMO"}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Este registro corresponde a una orden de demostración validada en Firestore. Puedes consultar su estado en la sección Perfil > Historial de pedidos.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onBack,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Volver a la tienda")
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
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal:", style = MaterialTheme.typography.bodyMedium)
                Text(formatCurrencyMxn(subtotal), style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Envío estimado:", style = MaterialTheme.typography.bodyMedium)
                Text(formatCurrencyMxn(shipping), style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total (Validado en Backend):",
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
                    .height(52.dp)
                    .testTag("checkout_continue_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(buttonText, style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.core.database.PenolesLocalDatabase
import com.example.core.datastore.UserPreferencesRepository
import com.example.core.model.NewsArticle
import com.example.core.model.PenolesReferenceCatalog
import com.example.core.model.Product
import com.example.core.security.configureAppCheck
import com.example.data.repository.PenolesCloudRepository
import com.example.feature.assistant.AssistantScreen
import com.example.feature.assistant.ProfileAndAdminScreen
import com.example.feature.auth.LoginScreen
import com.example.feature.auth.LoginViewModel
import com.example.feature.auth.signOutUser
import com.example.feature.home.HomeScreen
import com.example.feature.main.PenolesMainViewModel
import com.example.feature.news.GlobalSearchScreen
import com.example.feature.news.NewsDetailScreen
import com.example.feature.news.NewsScreen
import com.example.feature.store.CartAndCheckoutScreen
import com.example.feature.store.ProductDetailScreen
import com.example.feature.store.StoreScreen
import com.example.ui.theme.PenolesConectaTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

const val ROUTE_LOGIN = "login"
const val ROUTE_HOME = "home"
const val ROUTE_GUEST_NEWS = "guest_news"

enum class MainNavTab(val label: String, val icon: ImageVector, val testTag: String) {
    HOME("Inicio", Icons.Default.Home, "nav_tab_home"),
    NEWS("Noticias", Icons.Default.Newspaper, "nav_tab_news"),
    STORE("Tienda", Icons.Default.Storefront, "nav_tab_store"),
    ASSISTANT("Asistente", Icons.Default.AutoAwesome, "nav_tab_assistant"),
    PROFILE("Perfil", Icons.Default.Person, "nav_tab_profile")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            PenolesRootApp()
        }
    }
}

@Composable
fun PenolesRootApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val loginUiState by loginViewModel.uiState.collectAsStateWithLifecycle()

    val preferencesRepository = remember { UserPreferencesRepository(context) }
    val localPrefs by preferencesRepository.preferencesFlow.collectAsStateWithLifecycle(
        initialValue = com.example.core.datastore.LocalUserPreferences()
    )

    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    PenolesConectaTheme(darkTheme = localPrefs.darkThemeEnabled) {
        NavHost(
            navController = navController,
            startDestination = ROUTE_LOGIN
        ) {
            composable(ROUTE_LOGIN) {
                LaunchedEffect(localPrefs.activeSessionUsername, currentUser) {
                    if (localPrefs.activeSessionUsername.isNotBlank() || currentUser != null) {
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(ROUTE_LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                LoginScreen(
                    loginViewModel = loginViewModel,
                    onNavigateToHome = { username, role, email ->
                        scope.launch {
                            preferencesRepository.saveLocalSession(
                                username = username,
                                role = role,
                                email = email
                            )
                        }
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(ROUTE_LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onGoogleAuthSuccess = {
                        currentUser = Firebase.auth.currentUser
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(ROUTE_LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onBrowsePublicNewsOffline = {
                        navController.navigate(ROUTE_GUEST_NEWS) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(ROUTE_HOME) {
                val authenticatedUser = currentUser
                val sessionUsername = loginUiState.authenticatedUsername.ifBlank {
                    localPrefs.activeSessionUsername
                }
                val sessionRole = loginUiState.authenticatedRole.ifBlank {
                    localPrefs.activeSessionRole.ifBlank { "ADMINISTRADOR" }
                }
                val sessionEmail = loginUiState.authenticatedEmail.ifBlank {
                    localPrefs.activeSessionEmail.ifBlank {
                        if (sessionUsername.isNotBlank()) "${sessionUsername.lowercase()}@penolesconecta.mx" else ""
                    }
                }

                val resolvedUserId = when {
                    sessionUsername.isNotBlank() -> "local_${sessionUsername.lowercase()}"
                    authenticatedUser != null -> authenticatedUser.uid
                    else -> "local_matrix"
                }
                val resolvedUserName = when {
                    sessionUsername.isNotBlank() -> sessionUsername
                    authenticatedUser != null -> authenticatedUser.displayName ?: "Usuario Peñoles Conecta"
                    else -> "Matrix"
                }
                val resolvedUserEmail = when {
                    sessionEmail.isNotBlank() -> sessionEmail
                    authenticatedUser != null -> authenticatedUser.email ?: "usuario@penolesconecta.mx"
                    else -> "matrix@penolesconecta.mx"
                }
                val resolvedRole = when {
                    sessionUsername.isNotBlank() -> sessionRole
                    resolvedUserEmail == "aldo.jael123@gmail.com" -> "ADMINISTRADOR"
                    else -> "USUARIO_ESTANDAR"
                }

                AuthenticatedPenolesApp(
                    currentUserId = resolvedUserId,
                    currentUserName = resolvedUserName,
                    currentUserEmail = resolvedUserEmail,
                    currentUserRole = resolvedRole,
                    onSignedOut = {
                        loginViewModel.resetAuthState()
                        scope.launch {
                            preferencesRepository.clearLocalSession()
                        }
                        currentUser = null
                        navController.navigate(ROUTE_LOGIN) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(ROUTE_GUEST_NEWS) {
                GuestOfflineNewsReaderScreen(
                    onBackToSignIn = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun AuthenticatedPenolesApp(
    currentUserId: String,
    currentUserName: String,
    currentUserEmail: String,
    currentUserRole: String,
    onSignedOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    // Keyed by currentUserId so switching accounts creates a fresh ViewModel instance
    val viewModel: PenolesMainViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                val localDb = PenolesLocalDatabase.getInstance(app)
                val prefsRepo = UserPreferencesRepository(app)
                PenolesMainViewModel(
                    cloudRepository = PenolesCloudRepository(db),
                    localDao = localDb.penolesLocalDao(),
                    preferencesRepository = prefsRepo,
                    currentUserId = currentUserId,
                    currentUserName = currentUserName,
                    currentUserEmail = currentUserEmail,
                    currentUserRole = currentUserRole
                )
            }
        }
    )

    val banners by viewModel.activeBanners.collectAsStateWithLifecycle()
    val news by viewModel.newsArticles.collectAsStateWithLifecycle()
    val products by viewModel.allProducts.collectAsStateWithLifecycle()
    val selectedNewsCategory by viewModel.selectedNewsCategory.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val orders by viewModel.userOrders.collectAsStateWithLifecycle()
    val quotes by viewModel.userQuotes.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfileState.collectAsStateWithLifecycle()
    val preferences by viewModel.preferencesState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.globalSearchResults.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val assistantState by viewModel.assistantUiState.collectAsStateWithLifecycle()
    val statusBannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    var currentTab by rememberSaveable { mutableStateOf(MainNavTab.HOME) }
    var selectedNewsDetail by remember { mutableStateOf<NewsArticle?>(null) }
    var selectedProductDetail by remember { mutableStateOf<Product?>(null) }
    var showGlobalSearch by rememberSaveable { mutableStateOf(false) }
    var showCartScreen by rememberSaveable { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusBannerMessage) {
        val msg = statusBannerMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusBannerMessage()
        }
    }

    val totalCartItemsCount = remember(cartItems) { cartItems.sumOf { it.quantity } }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 600.dp
        val isExpandedTablet = maxWidth >= 840.dp
        val isSplitNewsDetail = isExpandedTablet && currentTab == MainNavTab.NEWS && selectedNewsDetail != null && !showGlobalSearch && !showCartScreen
        val isSplitStoreDetail = isExpandedTablet && currentTab == MainNavTab.STORE && selectedProductDetail != null && !showGlobalSearch && !showCartScreen

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (!isTablet && selectedNewsDetail == null && selectedProductDetail == null && !showGlobalSearch && !showCartScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("main_bottom_navigation")
                    ) {
                        MainNavTab.entries.forEach { tab ->
                            val isSelected = currentTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isTablet) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        header = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
                            ) {
                                com.example.feature.common.CorporateBrandIdentityBadge(compact = true)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Conecta",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxHeight()
                            .testTag("main_navigation_rail")
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        MainNavTab.entries.forEach { tab ->
                            val isSelected = currentTab == tab
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = {
                                    currentTab = tab
                                    showGlobalSearch = false
                                    showCartScreen = false
                                    if (!isExpandedTablet) {
                                        selectedNewsDetail = null
                                        selectedProductDetail = null
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .testTag(tab.testTag)
                            )
                        }
                    }
                    VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    when {
                        showCartScreen -> {
                            CartAndCheckoutScreen(
                                cartItems = cartItems,
                                onBack = { showCartScreen = false },
                                onUpdateQuantity = { itemId, qty ->
                                    viewModel.updateCartItemQuantity(itemId, qty)
                                },
                                onRemoveItem = { itemId ->
                                    viewModel.removeCartItem(itemId)
                                },
                                onPlaceDemoOrder = { address, method, shipping, onSuccess ->
                                    viewModel.submitDemoCheckoutOrder(address, method, shipping, onSuccess)
                                }
                            )
                        }

                        showGlobalSearch -> {
                            GlobalSearchScreen(
                                query = searchQuery,
                                results = searchResults,
                                recentSearches = recentSearches,
                                onQueryChange = viewModel::updateSearchQuery,
                                onExecuteSearch = viewModel::recordSearchQuery,
                                onClearHistory = viewModel::clearSearchHistory,
                                onBack = { showGlobalSearch = false },
                                onNewsClick = { article ->
                                    showGlobalSearch = false
                                    selectedNewsDetail = article
                                },
                                onProductClick = { prod ->
                                    showGlobalSearch = false
                                    selectedProductDetail = prod
                                },
                                onCategoryClick = { cat ->
                                    showGlobalSearch = false
                                    viewModel.selectNewsCategory(cat)
                                    currentTab = MainNavTab.NEWS
                                }
                            )
                        }

                        isSplitNewsDetail -> {
                            val article = selectedNewsDetail!!
                            val isFav = favorites.any { it.targetType == "NEWS" && it.targetId == article.id }
                            val recommended = news.filter { it.id != article.id }
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.weight(0.46f).fillMaxHeight()) {
                                    NewsScreen(
                                        articles = news,
                                        selectedCategory = selectedNewsCategory,
                                        favorites = favorites,
                                        cartCount = totalCartItemsCount,
                                        onSelectCategory = viewModel::selectNewsCategory,
                                        onArticleClick = { selectedNewsDetail = it },
                                        onToggleFavorite = { art ->
                                            viewModel.toggleFavoriteItem(
                                                art.id,
                                                "NEWS",
                                                art.title,
                                                art.categoryId
                                            )
                                        },
                                        onSearchClick = { showGlobalSearch = true },
                                        onNotificationsClick = { showNotificationsDialog = true },
                                        onCartClick = { showCartScreen = true }
                                    )
                                }
                                VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Box(modifier = Modifier.weight(0.54f).fillMaxHeight()) {
                                    NewsDetailScreen(
                                        article = article,
                                        recommendedArticles = recommended,
                                        isFavorite = isFav,
                                        onBack = { selectedNewsDetail = null },
                                        onToggleFavorite = {
                                            viewModel.toggleFavoriteItem(
                                                article.id,
                                                "NEWS",
                                                article.title,
                                                article.categoryId
                                            )
                                        },
                                        onSelectRecommended = { selectedNewsDetail = it }
                                    )
                                }
                            }
                        }

                        isSplitStoreDetail -> {
                            val product = selectedProductDetail!!
                            val isFav = favorites.any { it.targetType == "PRODUCT" && it.targetId == product.id }
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.weight(0.46f).fillMaxHeight()) {
                                    StoreScreen(
                                        products = products,
                                        favorites = favorites,
                                        cartCount = totalCartItemsCount,
                                        onProductClick = { selectedProductDetail = it },
                                        onToggleFavorite = { prod ->
                                            viewModel.toggleFavoriteItem(
                                                prod.id,
                                                "PRODUCT",
                                                prod.name,
                                                prod.categoryId
                                            )
                                        },
                                        onOpenCart = { showCartScreen = true },
                                        onSearchClick = { showGlobalSearch = true },
                                        onNotificationsClick = { showNotificationsDialog = true }
                                    )
                                }
                                VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Box(modifier = Modifier.weight(0.54f).fillMaxHeight()) {
                                    ProductDetailScreen(
                                        product = product,
                                        isFavorite = isFav,
                                        userEmail = currentUserEmail,
                                        onBack = { selectedProductDetail = null },
                                        onToggleFavorite = {
                                            viewModel.toggleFavoriteItem(
                                                product.id,
                                                "PRODUCT",
                                                product.name,
                                                product.categoryId
                                            )
                                        },
                                        onAddToCart = { variant, qty ->
                                            viewModel.addProductToCart(product, variant, qty)
                                        },
                                        onOpenCart = {
                                            selectedProductDetail = null
                                            showCartScreen = true
                                        },
                                        onSubmitQuote = { company, email, phone, volume, notes ->
                                            viewModel.submitIndustrialQuoteRequest(
                                                product = product,
                                                companyName = company,
                                                contactEmail = email,
                                                contactPhone = phone,
                                                estimatedVolume = volume,
                                                applicationNotes = notes,
                                                onQuoteSuccess = {}
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        selectedNewsDetail != null -> {
                            val article = selectedNewsDetail!!
                            val isFav = favorites.any { it.targetType == "NEWS" && it.targetId == article.id }
                            val recommended = news.filter { it.id != article.id }
                            NewsDetailScreen(
                                article = article,
                                recommendedArticles = recommended,
                                isFavorite = isFav,
                                onBack = { selectedNewsDetail = null },
                                onToggleFavorite = {
                                    viewModel.toggleFavoriteItem(
                                        article.id,
                                        "NEWS",
                                        article.title,
                                        article.categoryId
                                    )
                                },
                                onSelectRecommended = { selectedNewsDetail = it }
                            )
                        }

                        selectedProductDetail != null -> {
                            val product = selectedProductDetail!!
                            val isFav = favorites.any { it.targetType == "PRODUCT" && it.targetId == product.id }
                            ProductDetailScreen(
                                product = product,
                                isFavorite = isFav,
                                userEmail = currentUserEmail,
                                onBack = { selectedProductDetail = null },
                                onToggleFavorite = {
                                    viewModel.toggleFavoriteItem(
                                        product.id,
                                        "PRODUCT",
                                        product.name,
                                        product.categoryId
                                    )
                                },
                                onAddToCart = { variant, qty ->
                                    viewModel.addProductToCart(product, variant, qty)
                                },
                                onOpenCart = {
                                    selectedProductDetail = null
                                    showCartScreen = true
                                },
                                onSubmitQuote = { company, email, phone, volume, notes ->
                                    viewModel.submitIndustrialQuoteRequest(
                                        product = product,
                                        companyName = company,
                                        contactEmail = email,
                                        contactPhone = phone,
                                        estimatedVolume = volume,
                                        applicationNotes = notes,
                                        onQuoteSuccess = {}
                                    )
                                }
                            )
                        }

                        else -> {
                            if (currentTab != MainNavTab.HOME) {
                                BackHandler { currentTab = MainNavTab.HOME }
                            }
                            when (currentTab) {
                                MainNavTab.HOME -> {
                                    HomeScreen(
                                        banners = banners,
                                        news = news,
                                        products = products,
                                        favorites = favorites,
                                        cartCount = totalCartItemsCount,
                                        autoAdvanceCarousel = preferences.autoAdvanceCarousel,
                                        onSearchClick = { showGlobalSearch = true },
                                        onNotificationsClick = { showNotificationsDialog = true },
                                        onCartClick = { showCartScreen = true },
                                        onBannerClick = { banner ->
                                            when (banner.targetType) {
                                                "NEWS" -> {
                                                    val matched = news.find { it.id == banner.targetId } ?: news.firstOrNull()
                                                    selectedNewsDetail = matched
                                                }
                                                "STORE" -> {
                                                    val matched = products.find { it.id == banner.targetId } ?: products.firstOrNull()
                                                    selectedProductDetail = matched
                                                }
                                                else -> currentTab = MainNavTab.ASSISTANT
                                            }
                                        },
                                        onQuickCategoryClick = { category ->
                                            val targetCategory = if (category == "Noticias") "Todas" else category
                                            viewModel.selectNewsCategory(targetCategory)
                                            currentTab = MainNavTab.NEWS
                                        },
                                        onNewsClick = { selectedNewsDetail = it },
                                        onViewAllNewsClick = {
                                            viewModel.selectNewsCategory("Todas")
                                            currentTab = MainNavTab.NEWS
                                        },
                                        onProductClick = { selectedProductDetail = it },
                                        onViewAllStoreClick = { currentTab = MainNavTab.STORE },
                                        onToggleFavorite = viewModel::toggleFavoriteItem,
                                        onAssistantCtaClick = { currentTab = MainNavTab.ASSISTANT }
                                    )
                                }

                                MainNavTab.NEWS -> {
                                    NewsScreen(
                                        articles = news,
                                        selectedCategory = selectedNewsCategory,
                                        favorites = favorites,
                                        cartCount = totalCartItemsCount,
                                        onSelectCategory = viewModel::selectNewsCategory,
                                        onArticleClick = { selectedNewsDetail = it },
                                        onToggleFavorite = { article ->
                                            viewModel.toggleFavoriteItem(
                                                article.id,
                                                "NEWS",
                                                article.title,
                                                article.categoryId
                                            )
                                        },
                                        onSearchClick = { showGlobalSearch = true },
                                        onNotificationsClick = { showNotificationsDialog = true },
                                        onCartClick = { showCartScreen = true }
                                    )
                                }

                                MainNavTab.STORE -> {
                                    StoreScreen(
                                        products = products,
                                        favorites = favorites,
                                        cartCount = totalCartItemsCount,
                                        onProductClick = { selectedProductDetail = it },
                                        onToggleFavorite = { product ->
                                            viewModel.toggleFavoriteItem(
                                                product.id,
                                                "PRODUCT",
                                                product.name,
                                                product.categoryId
                                            )
                                        },
                                        onOpenCart = { showCartScreen = true },
                                        onSearchClick = { showGlobalSearch = true },
                                        onNotificationsClick = { showNotificationsDialog = true }
                                    )
                                }

                                MainNavTab.ASSISTANT -> {
                                    AssistantScreen(
                                        messages = chatMessages,
                                        uiState = assistantState,
                                        cartCount = totalCartItemsCount,
                                        onSendPrompt = viewModel::askAssistant,
                                        onNewsCardClick = { selectedNewsDetail = it },
                                        onProductCardClick = { selectedProductDetail = it },
                                        onActionClick = { action ->
                                            when (action.routeType) {
                                                "TAB_NEWS" -> currentTab = MainNavTab.NEWS
                                                "TAB_STORE" -> currentTab = MainNavTab.STORE
                                                "TAB_PROFILE" -> currentTab = MainNavTab.PROFILE
                                            }
                                        },
                                        onSearchClick = { showGlobalSearch = true },
                                        onNotificationsClick = { showNotificationsDialog = true },
                                        onCartClick = { showCartScreen = true }
                                    )
                                }

                                MainNavTab.PROFILE -> {
                                    ProfileAndAdminScreen(
                                        userProfile = userProfile,
                                        preferences = preferences,
                                        favorites = favorites,
                                        orders = orders,
                                        quotes = quotes,
                                        cartCount = totalCartItemsCount,
                                        onToggleDarkTheme = viewModel::setDarkTheme,
                                        onToggleAutoCarousel = viewModel::setAutoAdvanceCarousel,
                                        onTogglePushConsent = viewModel::setPushNotificationsConsent,
                                        onToggleNotificationTopic = viewModel::toggleNotificationTopic,
                                        onCancelOrder = viewModel::cancelDemoOrder,
                                        onPublishNewsAdmin = { title, summary, content, category, author ->
                                            viewModel.publishAdminNewsArticle(title, summary, content, category, author) {}
                                        },
                                        onPublishProductAdmin = { name, desc, cat, sku, price, stock, type, specs ->
                                            viewModel.publishAdminProduct(name, desc, cat, sku, price, stock, type, specs) {}
                                        },
                                        onSearchClick = { showGlobalSearch = true },
                                        onNotificationsClick = { showNotificationsDialog = true },
                                        onCartClick = { showCartScreen = true },
                                        onSignOut = {
                                            signOutUser(
                                                context = context,
                                                credentialManager = credentialManager,
                                                onSignOutComplete = onSignedOut,
                                                scope = scope
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Centro de Avisos Peñoles Conecta") },
            text = {
                Column {
                    Text(
                        text = "Categorías activas: ${preferences.subscribedNotificationTopics.joinToString(", ")}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "\n• Nuevo comunicado en Sostenibilidad: Innovación en gestión hídrica.\n• Convocatoria abierta: Simposio de Innovación Metalúrgica.\n• Catálogo Industrial actualizado con fichas técnicas de Plata y Zinc SHG.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun GuestOfflineNewsReaderScreen(
    onBackToSignIn: () -> Unit
) {
    BackHandler(onBack = onBackToSignIn)
    var selectedCategory by remember { mutableStateOf("Todas") }
    var selectedArticle by remember { mutableStateOf<NewsArticle?>(null) }

    if (selectedArticle != null) {
        NewsDetailScreen(
            article = selectedArticle!!,
            recommendedArticles = PenolesReferenceCatalog.referenceNews.filter { it.id != selectedArticle!!.id },
            isFavorite = false,
            onBack = { selectedArticle = null },
            onToggleFavorite = onBackToSignIn,
            onSelectRecommended = { selectedArticle = it }
        )
    } else {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onBackToSignIn,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Iniciar sesión para activar Tienda, Carrito, Favoritos y Asistente IA",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NewsScreen(
                    articles = PenolesReferenceCatalog.referenceNews,
                    selectedCategory = selectedCategory,
                    favorites = emptyList(),
                    cartCount = 0,
                    onSelectCategory = { selectedCategory = it },
                    onArticleClick = { selectedArticle = it },
                    onToggleFavorite = { onBackToSignIn() },
                    onSearchClick = onBackToSignIn,
                    onNotificationsClick = onBackToSignIn,
                    onCartClick = onBackToSignIn
                )
            }
        }
    }
}

package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.core.model.Banner
import com.example.core.model.CartItem
import com.example.core.model.FavoriteItem
import com.example.core.model.NewsArticle
import com.example.core.model.Order
import com.example.core.model.Product
import com.example.core.model.QuoteRequest
import com.example.core.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PenolesCloudRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    // Always initialize with R.string.firestore_database_id
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("El usuario debe iniciar sesión con Google antes de acceder a Firestore.")
    }

    // --- 1. PERFIL DE USUARIO ---

    fun observeUserProfile(): Flow<UserProfile?> = flow {
        val uid = requireUserId()
        val path = "users/$uid"
        emitAll(
            db.collection("users").document(uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObject(
                        UserProfile::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    throw error
                }
        )
    }

    suspend fun ensureOrCreateUserProfile(
        displayName: String,
        email: String
    ): Result<UserProfile> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                existing.toObject(
                    UserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ) ?: UserProfile(id = uid, userId = uid, displayName = displayName, email = email)
            } else {
                val safeName = displayName.trim().ifBlank { "Usuario Peñoles Conecta" }.take(100)
                val safeEmail = email.trim().ifBlank { "usuario@penolesconecta.mx" }.take(150)
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "email" to safeEmail,
                    "role" to "USUARIO_ESTANDAR",
                    "notificationsEnabled" to true,
                    "preferredCategories" to listOf("Sostenibilidad", "Innovación", "Seguridad"),
                    "language" to "es-MX",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
                UserProfile(
                    id = uid,
                    userId = uid,
                    displayName = safeName,
                    email = safeEmail,
                    role = "USUARIO_ESTANDAR",
                    notificationsEnabled = true
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun updateUserPreferences(
        displayName: String,
        notificationsEnabled: Boolean,
        preferredCategories: List<String>
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val updates = mapOf(
                "displayName" to displayName.trim().take(100),
                "notificationsEnabled" to notificationsEnabled,
                "preferredCategories" to preferredCategories.take(10),
                "language" to "es-MX",
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(updates).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- 2. CARRITO DE ARTÍCULOS PROMOCIONALES (/users/{userId}/cart_items) ---

    fun observeCartItems(): Flow<List<CartItem>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/cart_items"
        emitAll(
            db.collection("users").document(uid).collection("cart_items")
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CartItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun addOrIncrementCartItem(
        productId: String,
        productName: String,
        variant: String,
        quantityToAdd: Int,
        unitPriceSnapshot: Double
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val safeVariant = variant.trim().ifBlank { "Estándar" }.take(60)
        val sanitizedItemId = "${productId}_${safeVariant.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")}".take(120)
        val docRef = db.collection("users").document(uid)
            .collection("cart_items").document(sanitizedItemId)
        try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                val currentQty = (existing.getLong("quantity") ?: 1L).toInt()
                val newQty = (currentQty + quantityToAdd).coerceIn(1, 99)
                docRef.update(
                    mapOf(
                        "quantity" to newQty,
                        "variant" to safeVariant,
                        "unitPriceSnapshot" to unitPriceSnapshot,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "productId" to productId.take(128),
                    "productName" to productName.take(150),
                    "variant" to safeVariant,
                    "quantity" to quantityToAdd.coerceIn(1, 99),
                    "unitPriceSnapshot" to unitPriceSnapshot,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            sanitizedItemId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun getCartItemById(targetUserId: String, itemId: String): Result<CartItem?> = runCatching {
        val docRef = db.collection("users").document(targetUserId)
            .collection("cart_items").document(itemId)
        try {
            val snapshot = docRef.get().await()
            snapshot.toObject(
                CartItem::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun updateCartItemQuantity(itemId: String, newQuantity: Int): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("cart_items").document(itemId)
        try {
            if (newQuantity <= 0) {
                docRef.delete().await()
            } else {
                docRef.update(
                    mapOf(
                        "quantity" to newQuantity.coerceIn(1, 99),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun removeCartItem(itemId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("cart_items").document(itemId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun clearCart(items: List<CartItem>): Result<Unit> = runCatching {
        val uid = requireUserId()
        items.forEach { item ->
            if (item.id.isNotBlank()) {
                val docRef = db.collection("users").document(uid)
                    .collection("cart_items").document(item.id)
                try {
                    docRef.delete().await()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    handleFirestoreError(e, OperationType.DELETE, docRef.path)
                    throw e
                }
            }
        }
    }

    // --- 3. FAVORITOS (/users/{userId}/favorites) ---

    fun observeFavorites(): Flow<List<FavoriteItem>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/favorites"
        emitAll(
            db.collection("users").document(uid).collection("favorites")
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        FavoriteItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun toggleFavorite(
        targetId: String,
        targetType: String,
        title: String,
        category: String
    ): Result<Boolean> = runCatching {
        val uid = requireUserId()
        val favId = "${targetType}_${targetId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")}".take(120)
        val docRef = db.collection("users").document(uid)
            .collection("favorites").document(favId)
        try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                docRef.delete().await()
                false
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "targetId" to targetId.take(128),
                    "targetType" to targetType,
                    "title" to title.take(200),
                    "category" to category.ifBlank { "General" }.take(60),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
                true
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    // --- 4. PEDIDOS PROMOCIONALES (/orders) CON VALIDACIÓN DE TOTALES ---

    fun observeUserOrders(): Flow<List<Order>> = flow {
        val uid = requireUserId()
        val path = "orders"
        emitAll(
            db.collection("orders")
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        Order::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun placeDemoOrder(
        cartItems: List<CartItem>,
        shippingAddress: String,
        shippingMethod: String,
        shippingCost: Double
    ): Result<String> = runCatching {
        val uid = requireUserId()
        require(cartItems.isNotEmpty()) { "El carrito está vacío." }
        val subtotal = cartItems.sumOf { it.unitPriceSnapshot * it.quantity }
        val shipping = shippingCost.coerceAtLeast(0.0)
        val total = subtotal + shipping
        val itemCount = cartItems.sumOf { it.quantity }.coerceIn(1, 500)
        val itemsSummary = cartItems.joinToString("; ") { "${it.quantity}x ${it.productName} (${it.variant})" }.take(1000)
        val orderId = "ord_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val paymentRef = "DEMO-CHK-${orderId.takeLast(8).uppercase()}"

        val docRef = db.collection("orders").document(orderId)
        try {
            val payload = mapOf(
                "userId" to uid,
                "itemsSummary" to itemsSummary,
                "itemCount" to itemCount,
                "subtotal" to subtotal,
                "shipping" to shipping,
                "total" to total,
                "currency" to "MXN",
                "status" to "CONFIRMADO_DEMO",
                "shippingAddress" to shippingAddress.trim().take(400),
                "shippingMethod" to shippingMethod.trim().take(100),
                "paymentReference" to paymentRef,
                "isDemoOrder" to true,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            clearCart(cartItems)
            orderId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun cancelOrder(orderId: String): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection("orders").document(orderId)
        try {
            docRef.update(
                mapOf(
                    "status" to "CANCELADO",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- 5. COTIZACIONES INDUSTRIALES (/quote_requests) ---

    fun observeUserQuotes(): Flow<List<QuoteRequest>> = flow {
        val uid = requireUserId()
        val path = "quote_requests"
        emitAll(
            db.collection("quote_requests")
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        QuoteRequest::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun submitIndustrialQuote(
        productId: String,
        productName: String,
        companyName: String,
        contactEmail: String,
        contactPhone: String,
        estimatedVolume: String,
        applicationNotes: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val quoteId = "cot_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val docRef = db.collection("quote_requests").document(quoteId)
        try {
            val payload = mapOf(
                "userId" to uid,
                "productId" to productId.take(128),
                "productName" to productName.take(150),
                "companyName" to companyName.trim().take(150),
                "contactEmail" to contactEmail.trim().take(150),
                "contactPhone" to contactPhone.trim().take(30),
                "estimatedVolume" to estimatedVolume.trim().take(120),
                "applicationNotes" to applicationNotes.trim().take(1000),
                "status" to "PENDIENTE",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            quoteId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    // --- 6. CMS: NOTICIAS, BANNERS Y PRODUCTOS ---

    fun observePublishedNews(): Flow<List<NewsArticle>> = flow {
        requireUserId()
        val path = "news_articles"
        emitAll(
            db.collection("news_articles")
                .whereEqualTo("status", "published")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        NewsArticle::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    fun observeActiveBanners(): Flow<List<Banner>> = flow {
        requireUserId()
        val path = "banners"
        emitAll(
            db.collection("banners")
                .whereEqualTo("active", true)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        Banner::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    fun observeActiveProducts(): Flow<List<Product>> = flow {
        requireUserId()
        val path = "products"
        emitAll(
            db.collection("products")
                .whereEqualTo("active", true)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        Product::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun publishNewsArticleAsAdmin(
        title: String,
        summary: String,
        content: String,
        categoryId: String,
        author: String,
        readingTimeMinutes: Int,
        tags: List<String>
    ): Result<String> = runCatching {
        requireUserId()
        val articleId = "news_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        val docRef = db.collection("news_articles").document(articleId)
        try {
            val payload = mapOf(
                "title" to title.trim().take(200),
                "summary" to summary.trim().take(600),
                "content" to content.trim().take(15000),
                "imageUrl" to "img_banner_mining_innovation",
                "categoryId" to categoryId.trim().take(60),
                "author" to author.trim().ifBlank { "Comunicación Institucional" }.take(120),
                "status" to "published",
                "sourceUrl" to "https://www.penoles.com.mx/",
                "readingTimeMinutes" to readingTimeMinutes.coerceIn(1, 60),
                "tags" to tags.take(10),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            articleId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun publishProductAsAdmin(
        name: String,
        description: String,
        categoryId: String,
        sku: String,
        price: Double,
        stock: Int,
        productType: String,
        technicalSpecs: String,
        variants: List<String>
    ): Result<String> = runCatching {
        requireUserId()
        val productId = "prod_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        val docRef = db.collection("products").document(productId)
        try {
            val payload = mapOf(
                "name" to name.trim().take(150),
                "description" to description.trim().take(2000),
                "categoryId" to categoryId.trim().take(60),
                "imageUrl" to if (productType == "INDUSTRIAL") "img_industrial_silver_zinc" else "img_promo_store_kit",
                "sku" to sku.trim().take(64),
                "price" to if (productType == "INDUSTRIAL") 0.0 else price.coerceIn(0.0, 500000.0),
                "currency" to "MXN",
                "stock" to if (productType == "INDUSTRIAL") 0 else stock.coerceIn(0, 100000),
                "variants" to variants.take(10),
                "active" to true,
                "productType" to productType,
                "technicalSpecs" to technicalSpecs.trim().take(3000),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            productId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }
}

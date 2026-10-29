package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class PenolesCloudRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun cartAndOrder_authenticatedOwner_createsCartAndPlacesValidatedDemoOrder() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = PenolesCloudRepository(firestore, auth)
        val uniqueProdId = "promo_termo_${UUID.randomUUID().toString().take(8)}"

        val addResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.addOrIncrementCartItem(
                productId = uniqueProdId,
                productName = "Termo Institucional 600ml",
                variant = "Grafito",
                quantityToAdd = 2,
                unitPriceSnapshot = 450.0
            )
        }
        assertTrue(addResult.isSuccess)

        val emittedCart = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeCartItems().first { list -> list.any { it.productId == uniqueProdId } }
        }
        val targetItem = emittedCart.first { it.productId == uniqueProdId }
        assertEquals(2, targetItem.quantity)

        val orderResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.placeDemoOrder(
                cartItems = listOf(targetItem),
                shippingAddress = "Blvd. Independencia 2000, Torreón, Coahuila",
                shippingMethod = "Envío Estándar",
                shippingCost = 120.0
            )
        }
        assertTrue("Expected demo order creation to succeed, got: ${orderResult.exceptionOrNull()}", orderResult.isSuccess)
        val orderId = orderResult.getOrThrow()
        assertTrue(orderId.isNotBlank())

        val orders = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserOrders().first { list -> list.any { it.id == orderId } }
        }
        val createdOrder = orders.first { it.id == orderId }
        assertEquals(1020.0, createdOrder.total, 0.01)
    }

    @Test
    fun getCartItem_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = PenolesCloudRepository(firestore, auth)
        val uniqueProdId = "promo_gorra_${UUID.randomUUID().toString().take(8)}"

        val itemId = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.addOrIncrementCartItem(
                productId = uniqueProdId,
                productName = "Gorra Conecta",
                variant = "Unitalla",
                quantityToAdd = 1,
                unitPriceSnapshot = 320.0
            ).getOrThrow()
        }

        // Bob signs in and attempts to read Alice's cart item
        signInTestUser(BOB_EMAIL)
        val bobRepo = PenolesCloudRepository(firestore, auth)
        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getCartItemById(aliceUid, itemId)
        }
        assertTrue(result.isFailure)
        val firestoreException = result.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull("Expected FirebaseFirestoreException", firestoreException)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
    }

    @Test
    fun observeOrders_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val unauthFirestoreQuery = firestore.collection("orders").snapshots()
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                unauthFirestoreQuery.first()
            }
            fail("Expected PERMISSION_DENIED for unauthenticated query")
        } catch (e: Exception) {
            val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice@penolesconecta.test"
        const val BOB_EMAIL = "bob@penolesconecta.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}

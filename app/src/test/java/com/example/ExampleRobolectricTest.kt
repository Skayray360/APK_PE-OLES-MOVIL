package com.example

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.example.feature.auth.LoginViewModel
import com.example.feature.common.isValidRemoteHttpUrl
import com.example.feature.common.resolveEditorialOrProductDrawable
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkPrimary
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightPrimary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Peñoles Conecta", appName)
    }

    @Test
    fun `LoginViewModel validates Matrix and Matrix123 credentials and updates state`() {
        val viewModel = LoginViewModel()

        // Credenciales incorrectas deben ser rechazadas
        viewModel.onUsernameChanged("Matrix")
        viewModel.onPasswordChanged("WrongPass")
        assertFalse(viewModel.validateCredentials())
        assertFalse(viewModel.uiState.value.isAuthenticated)

        // Credenciales válidas ('Matrix' / 'Matrix123') deben autenticar con rol ADMINISTRADOR
        viewModel.onUsernameChanged("Matrix")
        viewModel.onPasswordChanged("Matrix123")
        assertTrue(viewModel.validateCredentials())
        assertTrue(viewModel.uiState.value.isAuthenticated)
        assertEquals("Matrix", viewModel.uiState.value.authenticatedUsername)
        assertEquals("ADMINISTRADOR", viewModel.uiState.value.authenticatedRole)
    }

    @Test
    fun `PenolesStoreAssetRegistry assigns distinct placeholders per store product and retires Global Solutions kit`() {
        val productIds = listOf(
            "promo_termo_acero",
            "promo_gorra_institucional",
            "promo_libreta_ejecutiva",
            "promo_playera_polo",
            "promo_mochila_ejecutiva"
        )

        val resolvedDrawables = productIds.map { id ->
            val resId = resolveEditorialOrProductDrawable(
                imageKeyOrUrl = "img_promo_store_kit",
                productId = id,
                isProduct = true
            )
            // Must never return the retired "Global Solutions" kit image
            assertNotEquals(R.drawable.img_promo_store_kit_1793310481110, resId)
            resId
        }

        // All 5 products must resolve to 5 distinct drawables
        assertEquals(5, resolvedDrawables.toSet().size)
    }

    @Test
    fun `resolveEditorialOrProductDrawable distinguishes valid remote URLs and separates news from product fallbacks`() {
        assertTrue(isValidRemoteHttpUrl("https://www.penoles.com.mx/assets/termo.webp"))
        assertFalse(isValidRemoteHttpUrl("img_promo_store_kit"))
        assertFalse(isValidRemoteHttpUrl(""))

        val newsFallback = resolveEditorialOrProductDrawable(imageKeyOrUrl = "unknown_news_key", isProduct = false)
        val productFallback = resolveEditorialOrProductDrawable(imageKeyOrUrl = "unknown_product_key", isProduct = true)
        assertNotEquals(newsFallback, productFallback)
    }

    @Test
    fun `Corporate palette tokens match specification`() {
        assertEquals(Color(0xFFC41E24), LightPrimary)
        assertEquals(Color(0xFFFFFFFF), LightOnPrimary)
        assertEquals(Color(0xFFF5F6F8), LightBackground)
        assertEquals(Color(0xFFFFB3B5), DarkPrimary)
        assertEquals(Color(0xFF0E1117), DarkBackground)
    }
}


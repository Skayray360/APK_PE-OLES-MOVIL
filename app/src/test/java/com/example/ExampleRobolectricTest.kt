package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.feature.auth.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
}

package com.rtech.klarnapay.presentation.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rtech.klarnapay.domain.model.Order
import com.rtech.klarnapay.presentation.feature.checkout.CheckoutScreen
import com.rtech.klarnapay.presentation.feature.confirmation.ConfirmationScreen
import com.rtech.klarnapay.presentation.feature.products.ProductsScreen

// ── Route constants ───────────────────────────────────────────────────────────
private object Routes {
    const val PRODUCTS = "products"
    const val CHECKOUT = "checkout/{productId}"
    const val CONFIRMATION = "confirmation/{orderId}/{status}"

    fun checkout(productId: String) = "checkout/$productId"
    fun confirmation(orderId: String, status: String) =
        "confirmation/$orderId/$status"
}

/**
 * Single navigation graph for the entire app.
 * Products → Checkout → Confirmation
 */
@Composable
fun KlarnaNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.PRODUCTS
    ) {

        // ── Products screen ───────────────────────────────────────────────
        composable(Routes.PRODUCTS) {
            ProductsScreen(
                onNavigateToCheckout = { productId ->
                    navController.navigate(Routes.checkout(productId))
                }
            )
        }

        // ── Checkout screen ───────────────────────────────────────────────
        composable(
            route = Routes.CHECKOUT,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            CheckoutScreen(
                productId = productId,
                onNavigateToConfirmation = { order ->
                    navController.navigate(
                        Routes.confirmation(order.orderId, order.status)
                    ) {
                        // Clear checkout from back stack so back goes to products
                        popUpTo(Routes.PRODUCTS)
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Confirmation screen ───────────────────────────────────────────
        composable(
            route = Routes.CONFIRMATION,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType },
                navArgument("status") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val status = backStackEntry.arguments?.getString("status") ?: ""
            ConfirmationScreen(
                order = Order(orderId = orderId, redirectUrl = "", status = status),
                onBackToShop = {
                    navController.navigate(Routes.PRODUCTS) {
                        popUpTo(Routes.PRODUCTS) { inclusive = true }
                    }
                }
            )
        }
    }
}

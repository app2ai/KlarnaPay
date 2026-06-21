package com.rtech.klarnapay.presentation.feature.checkout


import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError
import com.rtech.klarnapay.domain.model.Order
import com.rtech.klarnapay.domain.model.PaymentMethodCategory
import com.rtech.klarnapay.presentation.feature.products.KlarnaPink
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

// Klarna return URL — must match scheme in AndroidManifest.xml
private const val KLARNA_RETURN_URL = "klarna-poc://return"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    productId: String,
    onNavigateToConfirmation: (Order) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel(parameters = { parametersOf(productId) })
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // ── Side-effect: navigate to confirmation when order is ready
    LaunchedEffect(state.order) {
        state.order?.let { order ->
            onNavigateToConfirmation(order)
            viewModel.onNavigatedToConfirmation()
        }
    }

    // ── Side-effect: navigate back
    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            onNavigateBack()
            viewModel.onNavigatedBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(CheckoutIntent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoadingProduct -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.product == null -> {
                    Text(
                        text = "Product not found.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    CheckoutContent(
                        state = state,
                        onIntent = viewModel::handleIntent,
                        onAuthorized = { authToken ->
                            viewModel.handleIntent(CheckoutIntent.OnKlarnaAuthorized(authToken))
                        },
                        onKlarnaError = { msg ->
                            viewModel.handleIntent(CheckoutIntent.OnKlarnaError(msg))
                        },
                    )
                }
            }

            // ── Error Snackbar
            state.error?.let { errorMsg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.handleIntent(CheckoutIntent.DismissError) }) {
                            Text("Dismiss")
                        }
                    }
                ) { Text(errorMsg) }
            }
        }
    }
}

@Composable
private fun CheckoutContent(
    state: CheckoutState,
    onIntent: (CheckoutIntent) -> Unit,
    onAuthorized: (String) -> Unit,
    onKlarnaError: (String) -> Unit
) {
    val product = state.product!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Product summary card ──────────────────────────────────────────
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        product.description, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        product.formattedPrice, style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // ── Order summary ─────────────────────────────────────────────────
        Card(shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Order Summary",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                SummaryRow("Subtotal", product.formattedPrice)
                SummaryRow("Shipping", "Free")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SummaryRow("Total", product.formattedPrice, bold = true)
            }
        }

        // ── Payment section ───────────────────────────────────────────────
        Text(
            "Pay with Klarna",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (!state.sessionReady) {
            // Show "Choose Payment" button to create session
            Button(
                onClick = { onIntent(CheckoutIntent.InitiatePayment) },
                enabled = !state.isCreatingSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KlarnaPink),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (state.isCreatingSession) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("See Payment Options", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // ── Payment method tabs ─────────────────────────────────────
            PaymentMethodTabs(
                categories = state.paymentMethodCategories,
                selectedId = state.selectedCategoryId,
                onSelectCategory = { onIntent(CheckoutIntent.SelectPaymentCategory(it)) }
            )

            // ── Klarna Payment View (SDK) ────────────────────────────────
            state.selectedCategoryId?.let { categoryId ->
                KlarnaPaymentSection(
                    clientToken = state.clientToken!!,
                    categoryId = categoryId,
                    returnUrl = KLARNA_RETURN_URL,
                    isLoading = state.isCreatingOrder,
                    onAuthorized = onAuthorized,
                    onError = onKlarnaError,
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── Payment Method Tabs ───────────────────────────────────────────────────────

@Composable
private fun PaymentMethodTabs(
    categories: List<PaymentMethodCategory>,
    selectedId: String?,
    onSelectCategory: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { category ->
            val isSelected = category.identifier == selectedId
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) KlarnaPink else MaterialTheme.colorScheme.outline
                ),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected)
                        KlarnaPink.copy(alpha = 0.12f)
                    else
                        MaterialTheme.colorScheme.surface
                ),
                onClick = { onSelectCategory(category.identifier) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                        Text(
                            text = categorySubtitle(category.identifier),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectCategory(category.identifier) },
                        colors = RadioButtonDefaults.colors(selectedColor = KlarnaPink)
                    )
                }
            }
        }
    }
}

/** Human-readable subtitle per category identifier. */
private fun categorySubtitle(identifier: String): String = when (identifier) {
    "pay_now" -> "Pay instantly with card or bank transfer"
    "pay_later" -> "Buy now, pay in 30 days — interest free"
    "pay_over_time" -> "Split into 4 easy payments every 2 weeks"
    else -> "Flexible payment options"
}

// ── Klarna SDK Payment Section ───────────────────────────────────────────────

@Composable
private fun KlarnaPaymentSection(
    clientToken: String,
    categoryId: String,
    returnUrl: String,
    isLoading: Boolean,
    onAuthorized: (String) -> Unit,
    onError: (String) -> Unit,
) {
    var sdkLoaded by remember(clientToken, categoryId) { mutableStateOf(false) }
    var paymentViewRef by remember { mutableStateOf<KlarnaPaymentView?>(null) }

    DisposableEffect(clientToken, categoryId) {
        onDispose {
            paymentViewRef = null
            sdkLoaded = false
        }
    }

    Card(
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text       = "Complete your payment",
                style      = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            // SDK 2.11.x constructor: (context, category, callback, returnURL)
            // No environment param — SDK detects it from the client_token
            AndroidView(
                factory = { ctx ->

                    val callback = object : KlarnaPaymentViewCallback {

                        override fun onInitialized(view: KlarnaPaymentView) {
                            // load() uses no args in 2.11.x
                            view.load(
                                args = null
                            )
                            // Fallback: show Pay button after 3s if onLoaded never fires
                            Handler(Looper.getMainLooper())
                                .postDelayed({ sdkLoaded = true }, 3000)
                        }

                        override fun onLoaded(view: KlarnaPaymentView) {
                            sdkLoaded = true
                        }

                        override fun onLoadPaymentReview(
                            view: KlarnaPaymentView,
                            showForm: Boolean
                        ) {
                            // Fires in playground instead of onLoaded
                            if (showForm) sdkLoaded = true
                        }

                        override fun onAuthorized(
                            view: KlarnaPaymentView,
                            approved: Boolean,
                            authToken: String?,
                            finalizedRequired: Boolean?
                        ) {
                            if (approved && authToken != null) onAuthorized(authToken)
                            else onError("Authorization not approved")
                        }

                        override fun onReauthorized(
                            view: KlarnaPaymentView,
                            approved: Boolean,
                            authToken: String?
                        ) {}

                        override fun onFinalized(
                            view: KlarnaPaymentView,
                            approved: Boolean,
                            authToken: String?
                        ) {
                            if (approved && authToken != null) onAuthorized(authToken)
                        }

                        override fun onErrorOccurred(
                            view: KlarnaPaymentView,
                            error: KlarnaPaymentsSDKError
                        ) {
                            onError(error.message)
                        }
                    }

                    // SDK 2.11.x clean constructor
                    KlarnaPaymentView(
                        context   = ctx,
                        category  = categoryId,
                        callback  = callback,
                        returnURL = returnUrl
                    ).also { view ->
                        // Set WRAP_CONTENT so Klarna controls its own height
                        view.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        view.initialize(clientToken, returnUrl)
                        paymentViewRef = view
                    }
                },
                // wrapContentHeight is critical — do NOT use heightIn() or fixed height
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )

            Spacer(Modifier.height(16.dp))

            if (!sdkLoaded) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Loading payment form...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Button(
                    onClick  = { paymentViewRef?.authorize(true, null) },
                    enabled  = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KlarnaPink),
                    shape  = RoundedCornerShape(10.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(22.dp),
                            color       = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Pay Now", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

//@Composable
//private fun KlarnaPaymentSection(
//    clientToken: String,
//    categoryId: String,
//    returnUrl: String,
//    isLoading: Boolean,
//    onAuthorized: (String) -> Unit,
//    onError: (String) -> Unit,
//) {
//    var sdkInitialized by remember(categoryId) { mutableStateOf(false) }
//    var sdkLoaded by remember(clientToken, categoryId) { mutableStateOf(false) }
//    var paymentViewRef by remember { mutableStateOf<KlarnaPaymentView?>(null) }
//
//    DisposableEffect(clientToken, categoryId) {
//        onDispose {
//            paymentViewRef = null
//            sdkLoaded = false
//        }
//    }
//
//    Card(
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//
//            Text(
//                text = "Complete your payment",
//                style = MaterialTheme.typography.labelLarge,
//                fontWeight = FontWeight.SemiBold
//            )
//            Spacer(Modifier.height(12.dp))
//
//            AndroidView(
//                factory = { ctx ->
//                    KlarnaPaymentView(context = ctx).also { view ->
//                        view.category = categoryId
//                        view.returnURL = returnUrl
//
//                        view.registerPaymentViewCallback(object : KlarnaPaymentViewCallback {
//
//                            override fun onInitialized(view: KlarnaPaymentView) {
//                                // SDK ready — now safe to load
//                                view.load(null)
//                                sdkInitialized = true
//                            }
//
//                            override fun onLoaded(view: KlarnaPaymentView) {
//                                // Form fully rendered — enable Pay button
//                                sdkLoaded = true
//                            }
//
//                            override fun onLoadPaymentReview(
//                                view: KlarnaPaymentView, showForm: Boolean
//                            ) {
//                            }
//
//                            override fun onAuthorized(
//                                view: KlarnaPaymentView,
//                                approved: Boolean,
//                                authToken: String?,
//                                finalizedRequired: Boolean?
//                            ) {
//                                if (approved && authToken != null) onAuthorized(authToken)
//                                else onError("Authorization not approved")
//                            }
//
//                            override fun onErrorOccurred(
//                                view: KlarnaPaymentView,
//                                error: KlarnaPaymentsSDKError
//                            ) {
//                                onError(error.message)
//                            }
//
//                            override fun onReauthorized(
//                                view: KlarnaPaymentView,
//                                approved: Boolean,
//                                authToken: String?
//                            ) {
//                            }
//
//                            override fun onFinalized(
//                                view: KlarnaPaymentView,
//                                approved: Boolean,
//                                authToken: String?
//                            ) {
//                                if (approved && authToken != null) onAuthorized(authToken)
//                            }
//
//                        })
//
//                        // ✅ Initialize with clientToken FIRST — load() is called in onInitialized
//                        view.initialize(clientToken, returnUrl)
//                        paymentViewRef = view
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .heightIn(min = 200.dp)
//            )
//
//            Spacer(Modifier.height(16.dp))
//
//            if (!sdkLoaded) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
//                    Spacer(Modifier.width(10.dp))
//                    Text(
//                        "Loading payment form...",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            } else {
//                Button(
//                    onClick = { paymentViewRef?.authorize(true, null) },
//                    enabled = !isLoading,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(52.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = KlarnaPink),
//                    shape = RoundedCornerShape(10.dp)
//                ) {
//                    if (isLoading) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.size(22.dp),
//                            color = Color.Black,
//                            strokeWidth = 2.dp
//                        )
//                    } else {
//                        Text("Pay Now", color = Color.Black, fontWeight = FontWeight.Bold)
//                    }
//                }
//            }
//        }
//    }
//}

// ── Helper composable ─────────────────────────────────────────────────────────

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label, style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value, style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (bold) MaterialTheme.colorScheme.primary else LocalContentColor.current
        )
    }
}
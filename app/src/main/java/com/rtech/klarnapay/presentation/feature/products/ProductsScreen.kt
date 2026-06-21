package com.rtech.klarnapay.presentation.feature.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rtech.klarnapay.domain.model.Product
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onNavigateToCheckout: (productId: String) -> Unit,
    viewModel: ProductsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // One-shot navigation side-effect
    LaunchedEffect(state.navigateToCheckout) {
        state.navigateToCheckout?.let { productId ->
            onNavigateToCheckout(productId)
            viewModel.onNavigatedToCheckout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text  = "Klarna Pay POC",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = "Select a product to checkout",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    ErrorView(
                        message  = state.error!!,
                        onRetry  = { viewModel.handleIntent(ProductsIntent.LoadProducts) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns            = GridCells.Fixed(2),
                        contentPadding     = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement   = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.products, key = { it.id }) { product ->
                            ProductCard(
                                product  = product,
                                onClick  = { viewModel.handleIntent(ProductsIntent.SelectProduct(product)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model              = product.imageUrl,
                contentDescription = product.name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )

            Column(modifier = Modifier.padding(12.dp)) {
                // Category chip
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text     = product.category,
                        style    = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text       = product.name,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )

                Text(
                    text     = product.description,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier       = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = product.formattedPrice,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )

                    // Klarna's signature "pink" buy button
                    Button(
                        onClick = onClick,
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = KlarnaPink
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text  = "Buy",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier             = modifier.padding(24.dp),
        horizontalAlignment  = Alignment.CenterHorizontally
    ) {
        Text(text = "⚠️ $message", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

// Klarna brand pink
val KlarnaPink = Color(0xFFFFB3C7)
package com.nammavastra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nammavastra.repository.CartRepository
import com.nammavastra.repository.GalleryRepository
import com.nammavastra.repository.StoryRepository
import com.nammavastra.repository.TrendRepository
import com.nammavastra.ui.components.AppDrawerContent
import com.nammavastra.ui.components.AppTopBar
import com.nammavastra.ui.components.BottomNavigationBar
import com.nammavastra.ui.components.defaultDrawerItems
import com.nammavastra.ui.navigation.AppRoutes
import com.nammavastra.ui.navigation.BottomDestination
import com.nammavastra.ui.screens.AuthScreen
import com.nammavastra.ui.screens.AdminScreen
import com.nammavastra.ui.screens.AccountHubScreen
import com.nammavastra.ui.screens.CartScreen
import com.nammavastra.ui.screens.LoomGalleryScreen
import com.nammavastra.ui.screens.PriceCalculatorScreen
import com.nammavastra.ui.screens.SareeDetailScreen
import com.nammavastra.ui.screens.StorySubmissionScreen
import com.nammavastra.ui.screens.TrendBoardScreen
import com.nammavastra.ui.screens.UploadSareeScreen
import com.nammavastra.ui.screens.WeaverStoryScreen
import com.nammavastra.ui.theme.NammaVastraTheme
import com.nammavastra.viewmodel.AuthViewModel
import com.nammavastra.viewmodel.AuthViewModelFactory
import com.nammavastra.viewmodel.AdminViewModel
import com.nammavastra.viewmodel.AdminViewModelFactory
import com.nammavastra.viewmodel.CartViewModel
import com.nammavastra.viewmodel.CartViewModelFactory
import com.nammavastra.viewmodel.GalleryViewModel
import com.nammavastra.viewmodel.GalleryViewModelFactory
import com.nammavastra.viewmodel.PriceViewModel
import com.nammavastra.viewmodel.PriceViewModelFactory
import com.nammavastra.viewmodel.StoryViewModel
import com.nammavastra.viewmodel.StoryViewModelFactory
import com.nammavastra.viewmodel.SubmissionViewModel
import com.nammavastra.viewmodel.SubmissionViewModelFactory
import com.nammavastra.viewmodel.TrendViewModel
import com.nammavastra.viewmodel.TrendViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            NammaVastraTheme {
                NammaVastraRoot()
            }
        }
    }
}

@Composable
private fun NammaVastraRoot() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = androidx.compose.ui.platform.LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val authState by authViewModel.uiState.collectAsState()
    val cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory(CartRepository(context)))
    val cartItems by cartViewModel.items.collectAsState()

    val trendViewModel: TrendViewModel = viewModel(
        factory = TrendViewModelFactory(TrendRepository(context))
    )
    val galleryViewModel: GalleryViewModel = viewModel(
        factory = GalleryViewModelFactory(GalleryRepository(context))
    )
    val priceViewModel: PriceViewModel = viewModel(factory = PriceViewModelFactory())
    val storyViewModel: StoryViewModel = viewModel(
        factory = StoryViewModelFactory(StoryRepository(context))
    )
    val submissionViewModel: SubmissionViewModel = viewModel(
        factory = SubmissionViewModelFactory(StoryRepository(context))
    )
    val adminViewModel: AdminViewModel = viewModel(
        factory = AdminViewModelFactory(
            storyRepository = StoryRepository(context),
            galleryRepository = GalleryRepository(context)
        )
    )

    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val currentBottom = BottomDestination.entries.firstOrNull { it.route == route }
    val title = when {
        route == AppRoutes.Auth -> "Namma-Vastra"
        route == AppRoutes.Upload -> "Namma-Vastra"
        route == AppRoutes.StorySubmission -> "Namma-Vastra"
        route == AppRoutes.Admin -> "Namma-Vastra"
        route?.startsWith("saree/") == true -> "Namma-Vastra"
        else -> "Namma-Vastra"
    }

    LaunchedEffect(authState.isAuthenticated, route) {
        if (!authState.isAuthenticated) {
            navController.navigate(AppRoutes.Auth) {
                popUpTo(0)
            }
        } else if (route == AppRoutes.Auth) {
            navController.navigate(BottomDestination.Trend.route) {
                popUpTo(AppRoutes.Auth) { inclusive = true }
            }
        }
    }

    LaunchedEffect(authState.currentUser?.uid, authState.resolvedEmail) {
        cartViewModel.switchOwner(authState.currentUser?.uid, authState.resolvedEmail)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                items = defaultDrawerItems(authState.isAdmin, cartItems.size),
                currentRoute = route,
                onItemClick = { item ->
                    scope.launch { drawerState.close() }
                    if (item.action == "sign_out") {
                        authViewModel.signOut()
                    } else if (item.route != null) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (route != AppRoutes.Auth) {
                    AppTopBar(
                        title = title,
                        showBack = route == AppRoutes.Upload ||
                            route == AppRoutes.StorySubmission ||
                            route == AppRoutes.Admin ||
                            route == AppRoutes.Account ||
                            route?.startsWith("saree/") == true ||
                            route == AppRoutes.Cart,
                        onBack = { navController.popBackStack() },
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }
            },
            bottomBar = {
                if (currentBottom != null) {
                    BottomNavigationBar(
                        current = currentBottom,
                        onSelected = { destination ->
                            navController.navigate(destination.route) {
                                popUpTo(BottomDestination.Trend.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            AppNavHost(
                padding = padding,
                navController = navController,
                authViewModel = authViewModel,
                trendViewModel = trendViewModel,
                galleryViewModel = galleryViewModel,
                cartViewModel = cartViewModel,
                cartCount = cartItems.size,
                priceViewModel = priceViewModel,
                storyViewModel = storyViewModel,
                submissionViewModel = submissionViewModel,
                adminViewModel = adminViewModel,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
private fun AppNavHost(
    padding: PaddingValues,
    navController: androidx.navigation.NavHostController,
    authViewModel: AuthViewModel,
    trendViewModel: TrendViewModel,
    galleryViewModel: GalleryViewModel,
    cartViewModel: CartViewModel,
    cartCount: Int,
    priceViewModel: PriceViewModel,
    storyViewModel: StoryViewModel,
    submissionViewModel: SubmissionViewModel,
    adminViewModel: AdminViewModel,
    snackbarHostState: SnackbarHostState
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Auth,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(AppRoutes.Auth) {
            AuthScreen(
                viewModel = authViewModel,
                modifier = Modifier.padding(padding)
            )
        }
        composable(BottomDestination.Trend.route) {
            TrendBoardScreen(
                viewModel = trendViewModel,
                modifier = Modifier.padding(padding)
            )
        }
        composable(BottomDestination.Gallery.route) {
            LoomGalleryScreen(
                viewModel = galleryViewModel,
                onOpenDetail = { navController.navigate(AppRoutes.sareeDetail(it)) },
                onUpload = { navController.navigate(AppRoutes.Upload) },
                modifier = Modifier.padding(padding)
            )
        }
        composable(BottomDestination.Calculator.route) {
            PriceCalculatorScreen(
                viewModel = priceViewModel,
                modifier = Modifier.padding(padding)
            )
        }
        composable(BottomDestination.Story.route) {
            WeaverStoryScreen(
                viewModel = storyViewModel,
                modifier = Modifier.padding(padding)
            )
        }
        composable(
            route = AppRoutes.SareeDetail,
            arguments = listOf(navArgument("sareeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sareeId = backStackEntry.arguments?.getString("sareeId").orEmpty()
            val scope = rememberCoroutineScope()
            val latestId by rememberUpdatedState(sareeId)
            val detailCartItems by cartViewModel.items.collectAsState()
            var sareeState by androidx.compose.runtime.remember {
                androidx.compose.runtime.mutableStateOf<com.nammavastra.model.Saree?>(null)
            }
            LaunchedEffect(latestId) {
                sareeState = galleryViewModel.getSaree(latestId)
                if (sareeState == null) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Saree details unavailable.")
                    }
                }
            }
            sareeState?.let { saree ->
                SareeDetailScreen(
                    saree = saree,
                    isInCart = detailCartItems.any { it.id == saree.id },
                    onToggleCart = { item ->
                        if (detailCartItems.any { it.id == item.id }) {
                            cartViewModel.remove(item.id)
                        } else {
                            cartViewModel.add(item)
                        }
                    },
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier.padding(padding)
                )
            }
        }
        composable(AppRoutes.Upload) {
            UploadSareeScreen(
                viewModel = galleryViewModel,
                snackbarHostState = snackbarHostState,
                onDone = { navController.popBackStack() },
                authViewModel = authViewModel,
                modifier = Modifier.padding(padding)
            )
        }
        composable(AppRoutes.Account) {
            AccountHubScreen(
                authViewModel = authViewModel,
                cartCount = cartCount,
                onOpenCart = { navController.navigate(AppRoutes.Cart) },
                modifier = Modifier.padding(padding)
            )
        }
        composable(AppRoutes.StorySubmission) {
            StorySubmissionScreen(
                authViewModel = authViewModel,
                viewModel = submissionViewModel,
                snackbarHostState = snackbarHostState,
                onDone = { navController.popBackStack() },
                modifier = Modifier.padding(padding)
            )
        }
        composable(AppRoutes.Cart) {
            CartScreen(
                viewModel = cartViewModel,
                onOpenDetail = { navController.navigate(AppRoutes.sareeDetail(it)) },
                onBrowseGallery = { navController.navigate(BottomDestination.Gallery.route) },
                modifier = Modifier.padding(padding)
            )
        }
        composable(AppRoutes.Admin) {
            if (authViewModel.uiState.value.isAdmin) {
                AdminScreen(
                    viewModel = adminViewModel,
                    modifier = Modifier.padding(padding)
                )
            } else {
                EmptyRouteScreen(
                    title = "Admin Only",
                    body = "Only the admin account can review stories and remove saree posts.",
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun EmptyRouteScreen(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.material3.Text(title, style = androidx.compose.material3.MaterialTheme.typography.displayMedium)
        androidx.compose.material3.Text(body, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
    }
}

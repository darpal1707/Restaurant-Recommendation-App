package com.darpal.foodlabrinthnew.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.darpal.foodlabrinthnew.ui.decide.DecideScreen
import com.darpal.foodlabrinthnew.ui.detail.RestaurantDetailScreen
import com.darpal.foodlabrinthnew.ui.explore.ExploreScreen
import com.darpal.foodlabrinthnew.ui.home.HomeScreen
import com.darpal.foodlabrinthnew.ui.map.MapScreen
import com.darpal.foodlabrinthnew.ui.navigation.Routes
import com.darpal.foodlabrinthnew.ui.navigation.TopLevelDestination
import com.darpal.foodlabrinthnew.ui.profile.ProfileScreen
import com.darpal.foodlabrinthnew.ui.results.ResultsScreen
import com.darpal.foodlabrinthnew.ui.saved.SavedScreen

@Composable
fun FoodLabrinthApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val showBottomBar = TopLevelDestination.entries.any { tab ->
        destination?.hierarchy?.any { it.route == tab.route } == true
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
            ) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { tab ->
                        val selected = destination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.navigateToTab(tab.route) },
                            icon = {
                                Icon(if (selected) tab.selectedIcon else tab.unselectedIcon, contentDescription = null)
                            },
                            label = { Text(stringResource(tab.label)) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        // Only the bottom inset is applied here; each screen handles the status bar itself.
        val bottomPadding = PaddingValues(bottom = padding.calculateBottomPadding())
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(bottomPadding).consumeWindowInsets(bottomPadding),
            enterTransition = { fadeIn(tween(220)) },
            exitTransition = { fadeOut(tween(180)) },
        ) {
            val openRestaurant: (String) -> Unit = { navController.navigate(Routes.detail(it)) }
            val back: () -> Unit = { navController.popBackStack() }

            composable(Routes.HOME) {
                HomeScreen(
                    onOpenRestaurant = openRestaurant,
                    onSearch = { navController.navigate(Routes.search(it)) },
                    onOpenCuisine = { navController.navigate(Routes.cuisine(it)) },
                    onOpenCuisines = { title, names -> navController.navigate(Routes.cuisines(title, names)) },
                    onDecide = { navController.navigate(Routes.DECIDE) },
                )
            }
            composable(Routes.EXPLORE) {
                ExploreScreen(
                    onSearch = { navController.navigate(Routes.search(it)) },
                    onOpenCuisine = { navController.navigate(Routes.cuisine(it)) },
                )
            }
            composable(Routes.MAP) { MapScreen(onOpenRestaurant = openRestaurant) }
            composable(Routes.SAVED) {
                SavedScreen(onOpenRestaurant = openRestaurant, onExplore = { navController.navigateToTab(Routes.EXPLORE) })
            }
            composable(Routes.PROFILE) { ProfileScreen() }
            composable(Routes.DECIDE) {
                DecideScreen(
                    onBack = back,
                    onShowResults = { route -> navController.navigate(route) },
                )
            }
            composable(
                route = Routes.RESULTS,
                arguments = Routes.RESULTS_ARGS.map { name ->
                    navArgument(name) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                },
            ) {
                ResultsScreen(onBack = back, onOpenRestaurant = openRestaurant)
            }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument(Routes.ARG_ID) { type = NavType.StringType }),
            ) {
                RestaurantDetailScreen(
                    onBack = back,
                    onSignIn = { navController.navigateToTab(Routes.PROFILE) },
                )
            }
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

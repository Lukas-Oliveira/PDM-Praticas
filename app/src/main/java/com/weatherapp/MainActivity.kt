package com.weatherapp

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.util.Consumer
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.weatherapp.api.WeatherService
import com.weatherapp.db.fb.FBAuth
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.db.local.LocalDB
import com.weatherapp.monitor.ForecastMonitor
import com.weatherapp.repo.Repository
import com.weatherapp.ui.CityDialog
import com.weatherapp.ui.MainViewModel
import com.weatherapp.ui.MainViewModelFactory
import com.weatherapp.ui.nav.BottomNavBar
import com.weatherapp.ui.nav.BottomNavItem
import com.weatherapp.ui.nav.MainNavHost
import com.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        val viewModel: MainViewModel by viewModels()

        setContent {

            val firebaseAuth = remember { FBAuth() }
            if (firebaseAuth.currentUser == null)
                finish()

            val email = firebaseAuth.currentUser?.email
            val firebaseDatabase = remember { FBDatabase() }
            val localDatabase = remember { LocalDB(this, databaseName = "$email.db") }
            val service = remember { WeatherService() }
            val repository = remember { Repository(firebaseDatabase, localDatabase, service) }
            val monitor = remember { ForecastMonitor(this, repository) }

            var showDialog by remember { mutableStateOf(value = false) }
            val navController = rememberNavController()
            val currentRoute = navController.currentBackStackEntryAsState()
            val showButton = currentRoute.value?.destination?.route != BottomNavItem.MapPage.route
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {}
            )

            val viewModel: MainViewModel by viewModels {
                MainViewModelFactory(repository, service)
            }

            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { intent ->
                    val name = intent.getStringExtra("city")
                    viewModel.city = name
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }

            WeatherAppTheme {

                if (showDialog) {
                    CityDialog(
                        onDismiss = { showDialog = false },
                        onConfirm = { city ->
                            if (city.isNotBlank()) {
                                repository.addCity(name = city)
                            }
                            showDialog = false
                        }
                    )
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = "Bem vindo(a) ${viewModel.user?.name}")
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        Firebase.auth.signOut()
                                        this@MainActivity.finish()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ExitToApp,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavBar(navController = navController)
                    },
                    floatingActionButton = {
                        if (showButton) {
                            FloatingActionButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Adicionar")
                            }
                        }
                    }
                ) {
                    innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

                            MainNavHost(
                                navController = navController,
                                viewModel = viewModel,
                                context = this@MainActivity,
                                repository = repository
                            )
                        }
                }
            }
        }
    }
}

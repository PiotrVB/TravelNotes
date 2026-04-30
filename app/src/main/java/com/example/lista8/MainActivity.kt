package com.example.lista8

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lista8.data.Place
import com.example.lista8.ui.theme.Lista8Theme
import com.example.lista8.viewmodel.PlaceViewModel
import com.example.lista8.viewmodel.SettingsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            Lista8Theme(monochromatic = settingsViewModel.isMonochromatic) {
                TravelNotesApp(settingsViewModel)
            }
        }
    }
}

@Composable
fun TravelNotesApp(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        auth.signOut()
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("home") {
            MainScreen(navController, settingsViewModel)
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Travel Notes")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Uzupełnij dane", Toast.LENGTH_SHORT).show()
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Zalogowano", Toast.LENGTH_SHORT).show()
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Błąd logowania", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zaloguj")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nie masz konta? Zarejestruj się")
        }
    }
}

@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Rejestracja")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.length < 6) {
                    Toast.makeText(context, "Podaj e-mail i hasło min. 6 znaków", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Konto utworzone", Toast.LENGTH_SHORT).show()
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Błąd rejestracji", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zarejestruj")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Wróć do logowania")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(rootNavController: NavHostController, settingsViewModel: SettingsViewModel) {
    val innerNavController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val items = listOf(
        BottomItem("home_tab", "Home", Icons.Default.Home),
        BottomItem("map_tab", "Mapa", Icons.Default.LocationOn),
        BottomItem("cat_tab", "A wiesz że..", Icons.Default.Menu),
        BottomItem("settings_tab", "Settings", Icons.Default.Settings),
        BottomItem("contact_tab", "Contact", Icons.Default.ContactMail)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Travel Notes",
                    modifier = Modifier.padding(16.dp)
                )

                Divider()

                items.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            innerNavController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(innerNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Wyloguj") },
                    selected = false,
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        rootNavController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Travel Notes") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                innerNavController.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(innerNavController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NavHost(
                    navController = innerNavController,
                    startDestination = "home_tab"
                ) {
                    composable("home_tab") { HomeScreen() }
                    composable("map_tab") { MapScreen() }
                    composable("cat_tab") { TravelFactScreen() }
                    composable("settings_tab") { SettingsScreen(settingsViewModel) }
                    composable("contact_tab") { ContactScreen() }
                }
            }
        }
    }
}

data class BottomItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun HomeScreen(placeViewModel: PlaceViewModel = viewModel()) {
    val places by placeViewModel.places.collectAsState(initial = emptyList())
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp > 600

    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    if (isWideScreen) {
        // Wide screen layout: Side by side
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text("Dodaj nowe miejsce", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))
                
                PlaceInputFields(
                    name = name, onNameChange = { name = it },
                    city = city, onCityChange = { city = it },
                    description = description, onDescriptionChange = { description = it },
                    category = category, onCategoryChange = { category = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && city.isNotBlank()) {
                            placeViewModel.addPlace(name, city, description, category)
                            name = ""; city = ""; description = ""; category = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Dodaj miejsce")
                }
            }

            Column(
                modifier = Modifier.weight(1.5f)
            ) {
                Text("Twoja lista", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))
                PlaceList(places, placeViewModel)
            }
        }
    } else {
        // Compact layout: Vertical
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Miejsca do odwiedzenia")
            Spacer(modifier = Modifier.height(12.dp))

            PlaceInputFields(
                name = name, onNameChange = { name = it },
                city = city, onCityChange = { city = it },
                description = description, onDescriptionChange = { description = it },
                category = category, onCategoryChange = { category = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && city.isNotBlank()) {
                        placeViewModel.addPlace(name, city, description, category)
                        name = ""; city = ""; description = ""; category = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dodaj miejsce")
            }

            Spacer(modifier = Modifier.height(16.dp))
            PlaceList(places, placeViewModel)
        }
    }
}

@Composable
fun PlaceInputFields(
    name: String, onNameChange: (String) -> Unit,
    city: String, onCityChange: (String) -> Unit,
    description: String, onDescriptionChange: (String) -> Unit,
    category: String, onCategoryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name, onValueChange = onNameChange,
        label = { Text("Nazwa miejsca") }, modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = city, onValueChange = onCityChange,
        label = { Text("Miasto") }, modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = description, onValueChange = onDescriptionChange,
        label = { Text("Opis") }, modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = category, onValueChange = onCategoryChange,
        label = { Text("Kategoria") }, modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PlaceList(places: List<Place>, placeViewModel: PlaceViewModel) {
    LazyColumn {
        items(places) { place ->
            PlaceItem(
                place = place,
                onDelete = { placeViewModel.deletePlace(place) },
                onToggleVisited = { placeViewModel.toggleVisited(place) },
                onUpdate = { updatedPlace -> placeViewModel.updatePlace(updatedPlace) }
            )
        }
    }
}

@Composable
fun PlaceItem(
    place: Place,
    onDelete: () -> Unit,
    onToggleVisited: () -> Unit,
    onUpdate: (Place) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { showEditDialog = true }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(place.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text("Miasto: ${place.city}")
            Text("Kategoria: ${place.category}")
            Text("Opis: ${place.description}")

            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = place.visited,
                    onCheckedChange = { onToggleVisited() }
                )
                Text("Odwiedzone")
            }

            Row {
                Button(onClick = { showEditDialog = true }) {
                    Text("Edytuj")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onDelete) {
                    Text("Usuń")
                }
            }
        }
    }

    if (showEditDialog) {
        EditPlaceDialog(
            place = place,
            onDismiss = { showEditDialog = false },
            onSave = {
                onUpdate(it)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun EditPlaceDialog(
    place: Place,
    onDismiss: () -> Unit,
    onSave: (Place) -> Unit
) {
    var name by remember { mutableStateOf(place.name) }
    var city by remember { mutableStateOf(place.city) }
    var description by remember { mutableStateOf(place.description) }
    var category by remember { mutableStateOf(place.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edytuj miejsce") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nazwa") }
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Miasto") }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis") }
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategoria") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        place.copy(
                            name = name,
                            city = city,
                            description = description,
                            category = category
                        )
                    )
                }
            ) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val wroclaw = LatLng(51.1079, 17.0385)
    val university = LatLng(51.1107, 17.0335)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(wroclaw, 12f)
    }

    var locationText by remember { mutableStateOf("Lokalizacja nie została jeszcze pobrana") }
    var isLoadingLocation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Mapa")
        Spacer(modifier = Modifier.height(8.dp))

        Text(locationText)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (permissionState.status.isGranted) {
                    isLoadingLocation = true

                    val fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(context)

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            isLoadingLocation = false

                            if (location != null) {
                                val userLocation = LatLng(location.latitude, location.longitude)

                                locationText =
                                    "Twoja lokalizacja: ${location.latitude}, ${location.longitude}"

                                kotlinx.coroutines.MainScope().launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(userLocation, 14f)
                                    )
                                }
                            } else {
                                locationText = "Nie udało się pobrać lokalizacji"
                            }
                        }
                        .addOnFailureListener {
                            isLoadingLocation = false
                            locationText = "Błąd pobierania lokalizacji"
                        }
                } else {
                    permissionState.launchPermissionRequest()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pokaż moją lokalizację")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoadingLocation) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = university),
                    title = "Wybrana lokalizacja",
                    snippet = "Stały marker we Wrocławiu"
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Ustawienia", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Checkbox(
                checked = settingsViewModel.isMonochromatic,
                onCheckedChange = { settingsViewModel.toggleMonochromatic(it) }
            )
            Text("Tryb monochromatyczny")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Informacje o aplikacji", style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
        Text("Travel Notes v1.0")
        Text("Aplikacja pozwala na zarządzanie listą miejsc do odwiedzenia oraz podgląd lokalizacji na mapie.")

        Spacer(modifier = Modifier.height(16.dp))
        Text("Konto: ${com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: "Nie zalogowano"}")
    }
}

@Composable
fun ContactScreen() {
    val context = LocalContext.current

    fun openIntent(intent: Intent, errorMessage: String) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Contact")
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("kontakt@travelnotes.pl"))
                    putExtra(Intent.EXTRA_SUBJECT, "Kontakt z aplikacji Travel Notes")
                    putExtra(Intent.EXTRA_TEXT, "Dzień dobry,")
                }
                openIntent(intent, "Brak aplikacji pocztowej")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Wyślij e-mail")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:123456789"))
                openIntent(intent, "Brak dialera")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zadzwoń")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/travel/")
                )
                openIntent(intent, "Brak przeglądarki")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Otwórz stronę podróżniczą")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val uri = Uri.parse("geo:51.1079,17.0385?q=Wrocław")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                openIntent(intent, "Brak aplikacji map")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pokaż Wrocław na mapie")
        }
    }
}

@Composable
fun TravelFactScreen(viewModel: TravelViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Ciekawostka podróżnicza",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.padding(16.dp).heightIn(min = 100.dp)) {
                when (val state = viewModel.uiState) {
                    is TravelUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                    }

                    is TravelUiState.Success -> {
                        Text(
                            state.fact,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                        )
                    }

                    is TravelUiState.Error -> {
                        Text(
                            "Błąd pobierania danych. Sprawdź połączenie internetowe.",
                            color = Color.Red
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.getNewFact() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pobierz nową ciekawostkę")
        }
    }
}
package com.otorresd.ram

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.ExperimentalPagingApi
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.otorresd.ram.model.CharacterDetailViewModel
import com.otorresd.ram.model.CharactersListViewModel
import com.otorresd.ram.ui.Destinations
import com.otorresd.ram.ui.screens.CharacterDetail
import com.otorresd.ram.ui.screens.CharactersListC
import com.otorresd.ram.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalPagingApi
@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_RAM)
        super.onCreate(savedInstanceState)
        val imageLoader = ImageLoader.Builder(this)
            .componentRegistry {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder(this@MainActivity))
                } else {
                    add(GifDecoder())
                }
            }
            .build()
        Coil.setImageLoader(imageLoader)
        setContent {
            RAMTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    RamApp()
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalPagingApi
@Composable
fun RamApp(){
    val navController = rememberNavController()
    var isHome by remember { mutableStateOf(true) }
    navController.addOnDestinationChangedListener{ _, destination, _ ->
        isHome = destination.route == Destinations.Characters.name
    }
    var title by remember { mutableStateOf(Destinations.Characters.name) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp),
                onClick = { scope.launch { drawerState.close() } },
                content = { Text("Close Drawer") }
            )
        },
        content = {
            Column {
                TopAppBar(title = { Text(title) },
                    navigationIcon = if (!isHome){{
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        }
                    }} else {{
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = null)
                        }
                    }},
                    backgroundColor = TextOrange, contentColor = Color.White)
                RamNavHost(navController = navController){ title = it}
            }
        }
    )
}

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalPagingApi
@Composable
fun RamNavHost(navController: NavHostController, updateTitle: (String) -> Unit){
    NavHost(navController = navController, startDestination = Destinations.Characters.name){
        composable(Destinations.Characters.name) {
            updateTitle(Destinations.Characters.name)
            val charactersListViewModel = hiltViewModel<CharactersListViewModel>()
            CharactersListC(navController, charactersListViewModel)
        }
        composable("${Destinations.Detail.name}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })) {
            updateTitle(Destinations.Detail.name)
            val characterDetailViewModel = hiltViewModel<CharacterDetailViewModel>()
            CharacterDetail(it.arguments?.getString("id") ?: "", characterDetailViewModel)
        }
    }
}
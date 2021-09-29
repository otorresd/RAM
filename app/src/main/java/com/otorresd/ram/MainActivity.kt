package com.otorresd.ram

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
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
    RamNavHost(navController = navController)
}

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalPagingApi
@Composable
fun RamNavHost(navController: NavHostController){
    NavHost(navController = navController, startDestination = Destinations.Characters.name){
        composable(Destinations.Characters.name) {
            val charactersListViewModel = hiltViewModel<CharactersListViewModel>()
            CharactersListC(navController, charactersListViewModel)
        }
        composable("${Destinations.Detail.name}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })) {
            val characterDetailViewModel = hiltViewModel<CharacterDetailViewModel>()
            CharacterDetail(it.arguments?.getString("id") ?: "", characterDetailViewModel)
        }
    }
}
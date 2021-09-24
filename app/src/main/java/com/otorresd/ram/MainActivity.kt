package com.otorresd.ram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.Icon
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.liveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.LoadType
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.decode.DataSource
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.otorresd.ram.model.CharactersListViewModel
import com.otorresd.ram.room.entities.CharacterE
import com.otorresd.ram.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.engine.android.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_RAM)
        super.onCreate(savedInstanceState)
        setContent {
            RAMTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    CharactersListC()
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalPagingApi
@Composable
fun CharactersListC(charactersViewModel: CharactersListViewModel = viewModel()){
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val lazyPagingItems = charactersViewModel.pager.flow.collectAsLazyPagingItems()
    val state = rememberSwipeRefreshState(isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading )

    Box {
        SwipeRefresh(
            state = state,
            onRefresh = { lazyPagingItems.refresh() },
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Background)) {
                items(lazyPagingItems, key = { it.id }) { character ->
                    character?.let { CharacterC(character = character) } ?: CardHolderC()
                }

                lazyPagingItems.apply {
                    when {
                        loadState.refresh is LoadState.Error -> item {
                            NetworkConnectionError{ lazyPagingItems.refresh()}
                        }
                        loadState.append is LoadState.Loading -> item {
                            AppendProgress()
                        }
                        loadState.append is LoadState.Error -> item {
                            AppendRetryButton{ lazyPagingItems.retry() }
                        }
                    }
                }
            }
        }

        val showButton = listState.firstVisibleItemIndex > 0
        
        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
            ) {
            FloatingActionButton(onClick = {
                scope.launch {
                    listState.scrollToItem(0)
                }
            }, contentColor = Color.White,
            backgroundColor = TextOrange) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Localized description")
            }
        }
    }
}

@Composable
fun CardHolderC() {
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .padding(start = 10.dp, end = 10.dp),
        shape = CharacterCard.large,
        elevation = 5.dp) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            Image(painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight())
        }
    }
}

@Composable
fun CharacterC(character: CharacterE) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .padding(start = 10.dp, end = 10.dp),
        shape = CharacterCard.large,
        elevation = 5.dp,
        backgroundColor = CardBackground) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
        Image(painter = rememberImagePainter(character.image,
                builder = {
                    placeholder(R.drawable.ic_placeholder)
                    transformations(RoundedCornersTransformation())
                }),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(color = TransparentGray),
            ) {
                Text(text = character.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = TextOrange,
                    modifier = Modifier
                        .padding(start = 15.dp, bottom = 5.dp)
                        .align(Alignment.CenterVertically))
            }
        }
    }
}

@Composable
fun AppendProgress(){
    Column(modifier = Modifier
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center) {

        Card(shape = RoundedCornerShape(50),
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .align(Alignment.CenterHorizontally)) {
            CircularProgressIndicator(color = Color.Black)
        }
    }
}

@Composable
fun AppendRetryButton(retryAction: () -> Unit){
    Column(modifier = Modifier
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center) {

        Button(onClick = retryAction,
            colors = buttonColors(backgroundColor = TextOrange),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .align(Alignment.CenterHorizontally)) {
            Text(text = "Load More", color = Color.White)
        }
    }
}

@Composable
fun NetworkConnectionError(retryAction: () -> Unit){
    Column(modifier = Modifier
        .height(LocalConfiguration.current.screenHeightDp.dp)
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center) {
        Text(text = "Oops! Please connect your device to Internet",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            maxLines = 2,
            color = Color.White)
        Button(onClick = retryAction,
            colors = buttonColors(backgroundColor = TextOrange),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "Retry", color = Color.White)
        }
    }
}
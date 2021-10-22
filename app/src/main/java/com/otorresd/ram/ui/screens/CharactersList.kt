package com.otorresd.ram.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.imageLoader
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.otorresd.ram.model.CharactersListViewModel
import com.otorresd.ram.room.entities.CharacterE
import com.otorresd.ram.ui.theme.*
import com.otorresd.ram.R
import com.otorresd.ram.ui.Destinations
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalPagingApi
@Composable
fun CharactersListC(navHostController: NavHostController, charactersViewModel: CharactersListViewModel = viewModel()){
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val lazyPagingItems = charactersViewModel.pager.flow.collectAsLazyPagingItems()
    val state = rememberSwipeRefreshState(isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading )
    val items by charactersViewModel.charactersSize.collectAsState(initial = 0)
    val context = LocalContext.current
    val loadingImage = rememberImagePainter(R.drawable.time_portal,
        imageLoader = context.imageLoader,
    )
    val errorImagePainter = rememberImagePainter(R.drawable.morty_die,
        imageLoader = context.imageLoader,
    )
    Box {
        SwipeRefresh(
            state = state,
            onRefresh = { lazyPagingItems.refresh() },
            indicator = {state, trigger ->
                SwipeRefreshIndicator(state = state, refreshTriggerDistance = trigger, backgroundColor = PrimaryOrange, contentColor = Color.White)
            }
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(DarkBackground)) {
                items(lazyPagingItems, key = { it.id }) { character ->
                    character?.let { CharacterC(character = character, loadingImage = loadingImage, errorImagePainter = errorImagePainter){navHostController.navigate(
                        "${Destinations.Detail.name}/${character.id}")} } ?: CardHolderC()
                }

                lazyPagingItems.apply {
                    when {
                        loadState.refresh is LoadState.Error -> item {
                            if (items == 0)
                                NetworkConnectionError{ lazyPagingItems.refresh()}
                            else
                                AppendRetryButton{ lazyPagingItems.retry() }
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
                backgroundColor = PrimaryOrange,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier.size(40.dp)) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Localized description")
            }
        }
    }
}

@ExperimentalCoilApi
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
            val image = rememberImagePainter(R.drawable.time_portal, LocalContext.current.imageLoader)
            Image(painter = image,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight())
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalCoilApi
@Composable
fun CharacterC(character: CharacterE, loadingImage: ImagePainter, errorImagePainter: ImagePainter, onClick: () -> Unit) {
    var errorImage by remember { mutableStateOf(false) }
    Card(onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(start = 10.dp, end = 10.dp),
        shape = CharacterCard.large,
        elevation = 5.dp,
        backgroundColor = CardBackground
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            if (!errorImage){
                val image = rememberImagePainter(character.image,
                    imageLoader = LocalContext.current.imageLoader,
                    builder = {
                        error(R.drawable.ic_placeholder)
                        transformations(RoundedCornersTransformation())
                    },
                )

                when (image.state) {
                    is ImagePainter.State.Empty, is ImagePainter.State.Success -> {
                        Image(painter = image,
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight())
                    }
                    is ImagePainter.State.Loading -> {
                        Image(painter = loadingImage,
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight())
                    }
                    is ImagePainter.State.Error -> {
                        // If you wish to display some content if the request fails
                        errorImage = true
                    }
                    else -> {}
                }
            }else{
                Image(painter = errorImagePainter,
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight())

                FloatingActionButton(onClick = {
                    errorImage = false
                }, contentColor = Color.White,
                    backgroundColor = PrimaryOrange,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center)) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "")
                }
            }

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
                    color = PrimaryOrange,
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
            backgroundColor = PrimaryOrange,
            elevation = 8.dp,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .align(Alignment.CenterHorizontally)) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier
                .padding(5.dp))
        }
    }
}

@Composable
fun AppendRetryButton(retryAction: () -> Unit){
    Column(modifier = Modifier
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center) {

        Button(onClick = retryAction,
            colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryOrange),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .align(Alignment.CenterHorizontally)) {
            Text(text = "Load More", color = Color.White)
        }
    }
}

@ExperimentalCoilApi
@Composable
fun NetworkConnectionError(retryAction: () -> Unit){
    Column(modifier = Modifier
        .height(LocalConfiguration.current.screenHeightDp.dp)
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center) {
        val image = rememberImagePainter(R.drawable.morty_sun, LocalContext.current.imageLoader)
        Image(painter = image,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp))
        Text(text = "Oops! Please connect your device to Internet",
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
            maxLines = 2,
            color = Color.White)
        Button(onClick = retryAction,
            colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryOrange),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally)) {
            Text(text = "Retry", color = Color.White)
        }
    }
}
package com.otorresd.ram.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.imageLoader
import coil.transform.RoundedCornersTransformation
import com.otorresd.ram.R
import com.otorresd.ram.model.CharacterDetailViewModel
import com.otorresd.ram.ui.theme.*

@ExperimentalCoilApi
@Composable
fun CharacterDetail(id: String, viewModel: CharacterDetailViewModel = viewModel()) {
    val character by viewModel.getCharacterById(id).collectAsState(initial = null)
    character?.let {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .align(Alignment.Center),
                shape = CharacterCard.large,
                elevation = 5.dp,
                backgroundColor = CardBackground
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()) {
                    ImageCoil(it.image)
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, bottom = 10.dp)) {
                        Text(it.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 25.sp, maxLines = 2)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val color = when(it.status){
                                "Alive" -> Color.Green
                                "Dead" -> Color.Red
                                else -> Color.Gray
                            }
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text("${it.status} - ${it.species}", color = Color.White)   
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun ImageCoil(imageUrl: String){
    val context = LocalContext.current
    val loadingImage = rememberImagePainter(
        R.drawable.time_portal,
        imageLoader = context.imageLoader,
    )
    val errorImagePainter = rememberImagePainter(
        R.drawable.morty_die,
        imageLoader = context.imageLoader,
    )
    var errorImage by remember { mutableStateOf(false) }
    if (!errorImage){
        val image = rememberImagePainter(imageUrl,
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
                        .height(250.dp))
            }
            is ImagePainter.State.Loading -> {
                Image(painter = loadingImage,
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp))
            }
            is ImagePainter.State.Error -> {
                // If you wish to display some content if the request fails
                errorImage = true
            }
            else -> {}
        }
    }
    else{
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)) {
            Image(painter = errorImagePainter,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight())

            FloatingActionButton(onClick = {
                errorImage = false
            }, contentColor = Color.White,
                backgroundColor = TextOrange,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)) {
                Icon(Icons.Rounded.Refresh, contentDescription = "")
            }
        }
    }
}
package com.projecte.mewnagochi.screens.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.services.storage.Item
import com.projecte.mewnagochi.ui.furniture.MovableItem
import com.projecte.mewnagochi.ui.theme.Person
import java.util.UUID





@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel = viewModel()) {
    val uiState by homeScreenViewModel.uiState
    var isAddingFurniture by remember { mutableStateOf(false) }

    val userItems by homeScreenViewModel.items.collectAsState(emptyList())

    val person = Person()
    person.BuildSprite()

    Box () {
        Image(
            painter = painterResource(id = R.drawable.phone_backgrounds),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            colorFilter = if (uiState.isEditingFurniture) ColorFilter.tint(
                Color.DarkGray,
                BlendMode.Hardlight
            ) else null,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    if (isAddingFurniture) isAddingFurniture = false
                    else homeScreenViewModel.stopEditing()
                },
        )



        userItems.forEach {
            MovableItem(item= it)
        }
        //ListOfItems()
        val density = LocalDensity.current
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = !uiState.isEditingFurniture,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column {
                FilledIconButton(
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    modifier = Modifier
                        .padding(30.dp)
                        .size(60.dp),
                    onClick = { homeScreenViewModel.startEditing() }) {
                    Icon(Icons.Filled.Create, contentDescription = "edit background")
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = !isAddingFurniture && uiState.isEditingFurniture,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            FilledIconButton(
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,

                    ),
                modifier = Modifier
                    .padding(30.dp)
                    .size(60.dp),
                onClick = { isAddingFurniture = true }) {
                Icon(Icons.Filled.Add, contentDescription = "add furniture")
            }
            AnimatedVisibility(
                visible = uiState.isAnyFurnitureSelected,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FilledIconButton(
                    colors = IconButtonDefaults.filledIconButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                        containerColor = MaterialTheme.colorScheme.onError,
                    ),
                    modifier = Modifier
                        .padding(30.dp)
                        .size(60.dp),
                    onClick = {
                        homeScreenViewModel.deleteObject(uiState.selectedFurnitureId)
                    }) {
                    Icon(Icons.Filled.Delete, contentDescription = "delete furniture")
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = isAddingFurniture,
            enter = slideInHorizontally {
                with(density) { +40.dp.roundToPx() }
            },
            exit = slideOutHorizontally { with(density) { +40.dp.roundToPx() } } + fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .padding(30.dp),
                horizontalAlignment = Alignment.End
            ) {

                Card(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
                            .width(100.dp)
                    ) {
                        items(userItems){ item ->
                            Image(
                                modifier = Modifier.clickable {

                                    homeScreenViewModel.addItem(item= item)
                                    isAddingFurniture = false

                                },
                                painter = painterResource(id = item.res),
                                contentDescription = "",
                                colorFilter = if (item.visible  ) ColorFilter.tint(
                                    Color.DarkGray,
                                    BlendMode.Color
                                ) else null,
                            )
                        }
                    }
                }
            }
        }



        if (!uiState.isEditingFurniture && !isAddingFurniture) {
            person.Draw()
        }

    }


}

package com.projecte.mewnagochi.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
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
import com.projecte.mewnagochi.HomeScreenViewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.theme.Person
import java.util.UUID


@Composable
fun Torch() {
    com.projecte.mewnagochi.ui.furniture.Torch(
        id = UUID.randomUUID().toString(),
        res = R.drawable.torch
    ).Draw()
}

@Composable
fun Door() {
    com.projecte.mewnagochi.ui.furniture.Door(
        id = UUID.randomUUID().toString(),
        res = R.drawable.door
    ).Draw()
}

@Composable
fun Chest() {
    com.projecte.mewnagochi.ui.furniture.Chest(
        id = UUID.randomUUID().toString(),
        res = R.drawable.chest
    ).Draw()
}

@Composable
fun Window() {

    com.projecte.mewnagochi.ui.furniture.Window(
        id = UUID.randomUUID().toString(),
        res = R.drawable.window
    ).Draw()
}

@Composable
fun ListOfItems() {
    Torch()
    Window()
    Chest()
    Door()
}


@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel = viewModel()) {

    val isEditingFurniture by homeScreenViewModel.isEditingFurniture.collectAsState()
    val isAnyFurnitureSelected by homeScreenViewModel.isAnyFurnitureSelected.collectAsState()
    val selectedFurnitureId by homeScreenViewModel.selectedFurnitureId.collectAsState()
    val furniture by homeScreenViewModel.furniture.collectAsState()
    var isAddingFurniture by remember { mutableStateOf(false) }

    val furnitureIds = remember {
        listOf(
            R.drawable.window,
            R.drawable.chest,
            R.drawable.door,
            R.drawable.torch,
        )
    }


    val person = Person()
    person.BuildSprite()

    Box () {
        Image(
            painter = painterResource(id = R.drawable.phone_backgrounds),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            colorFilter = if (isEditingFurniture) ColorFilter.tint(
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


        ListOfItems()
        val density = LocalDensity.current
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = !isEditingFurniture,
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
            visible = !isAddingFurniture && isEditingFurniture,
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
                visible = isAnyFurnitureSelected,
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
                        homeScreenViewModel.deleteObject(selectedFurnitureId)
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
                    Column(
                        modifier = Modifier
                            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
                            .width(100.dp)
                    ) {
                        furnitureIds.forEach { furnitureId ->
                            Image(
                                modifier = Modifier.clickable {

                                    homeScreenViewModel.addObject(furnitureId)
                                    isAddingFurniture = false

                                },
                                painter = painterResource(id = furnitureId),
                                contentDescription = "",
                                colorFilter = if (furniture.contains(furnitureId)) ColorFilter.tint(
                                    Color.DarkGray,
                                    BlendMode.Color
                                ) else null,
                            )
                        }
                    }
                }
            }
        }



        if (!isEditingFurniture && !isAddingFurniture) {
            person.Draw()
        }

    }


}

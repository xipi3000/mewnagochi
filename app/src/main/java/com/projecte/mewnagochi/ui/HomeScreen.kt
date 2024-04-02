package com.projecte.mewnagochi.ui

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStoreFile
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.HomeScreenViewModel

import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.theme.MovableObject
import com.projecte.mewnagochi.ui.theme.Person1
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID




class Location(var x: Float, var y: Float)
@Composable
fun Torch(){
    MovableObject(id = UUID.randomUUID().toString(),res = R.drawable.window).Draw()
}
@Composable
fun Door(){
    MovableObject(id = UUID.randomUUID().toString(),res = R.drawable.ic_launcher_background).Draw()
}
@Composable
fun ListOfItems(furniture: MutableMap<Int, MutableState<Boolean>>) {

    //val move = MovableObject(id = UUID.randomUUID().toString(),res = R.drawable.window, fileName = "torch.json")
    //(furniture[R.drawable.window]?.value == true) LaunchedEffect(Unit) {
    //    launch { move.show() }
    //}
    //Torch()
    //Torch()
    //if(furniture[R.drawable.ic_launcher_background]?.value == true)
    Door()
    //Door()
    //Window()
    //Chest()
}



@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel = viewModel()) {

    val isEditingFurniture by homeScreenViewModel.isEditingFurniture.collectAsState()
    val _noteList = remember { MutableStateFlow(listOf<MovableObject>()) }
    val noteList by remember { _noteList }.collectAsState()
    val isAnyFurnitureSelected by homeScreenViewModel.isAnyFurnitureSelected.collectAsState()
    val selectedFurnitureId by homeScreenViewModel.selectedFurnitureId.collectAsState()
    val furnitures by homeScreenViewModel.furnitures.collectAsState()
    var isAddingFurniture by remember { mutableStateOf(false)}

    val furniture = remember {
        mutableMapOf(
            R.drawable.window to mutableStateOf(false),
            R.drawable.ic_launcher_background to mutableStateOf(false),
        )
    }


    val person1 = Person1()

    person1.BuildSprite()

    Box {
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
/*        noteList.forEach { note ->

            (if(furnitures[note.id]==null) Location(0F,0F) else furnitures[note.id])?.let { note.Draw(location = it) }
        }*/

        ListOfItems(furniture)
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
                        homeScreenViewModel.deleteSelected()
                        homeScreenViewModel.deleteObject(selectedFurnitureId)
                        furniture[selectedFurnitureId]?.value=false


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
                        furniture.forEach { (furnitureId, isShown)  ->
                            Image(
                                modifier = Modifier.clickable {
                                    //   homeScreenViewModel.stopEditing()
                                    homeScreenViewModel.addObject(furnitureId)
                                    furniture[furnitureId]?.value=true
                                    isAddingFurniture = false

                                },
                                painter = painterResource(id = furnitureId), contentDescription = "",
                                colorFilter = if (furniture[furnitureId]?.value==true) ColorFilter.tint(
                                    Color.DarkGray,
                                    BlendMode.Color
                                ) else null,
                            )
                        }
                    }

                }

            }

        }



        if (!isEditingFurniture) {
            person1.Draw()
        }

    }


}

@Composable
fun BackgroundEditor(isEditing: Boolean) {

}

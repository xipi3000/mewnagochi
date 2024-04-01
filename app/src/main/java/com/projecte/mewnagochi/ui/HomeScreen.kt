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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.projecte.mewnagochi.ui.theme.MovableObject
import com.projecte.mewnagochi.ui.theme.Person1
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

val furniture = listOf(
    R.drawable.window
)

class Location(var x: Float, var y: Float)


@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel = viewModel()) {
    var isEditing by remember { mutableStateOf(false) }
    var isEditingFurniture by remember { mutableStateOf(false) }
    val _noteList = remember { MutableStateFlow(listOf<MovableObject>()) }
    val noteList by remember { _noteList }.collectAsState()
    val isAnyFurnitureSelected by homeScreenViewModel.isAnyFurnitureSelected.collectAsState()
    val selectedFurnitureId by homeScreenViewModel.selectedFurnitureId.collectAsState()
    val furnitures by homeScreenViewModel.furnitures.collectAsState()
    val deltedId by homeScreenViewModel.deletedId.collectAsState()



    val person1 = Person1()

    person1.BuildSprite()

    Box {
        Image(
            painter = painterResource(id = R.drawable.phone_backgrounds),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            colorFilter = if (isEditing) ColorFilter.tint(
                Color.DarkGray,
                BlendMode.Hardlight
            ) else null,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    isEditing = false
                    isEditingFurniture = false
                },
        )
/*        noteList.forEach { note ->

            (if(furnitures[note.id]==null) Location(0F,0F) else furnitures[note.id])?.let { note.Draw(location = it) }
        }*/
        furnitures.forEach { (id,note) ->
            Log.i("delete",deltedId)
                if(note!=null)
                    note.Draw()

        }
        val density = LocalDensity.current
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = !isEditing,
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
                    onClick = { isEditing = true }) {
                    Icon(Icons.Filled.Create, contentDescription = "edit background")
                }


            }
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = isEditing && !isEditingFurniture,
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
                onClick = { isEditingFurniture = true }) {
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
                        //homeScreenViewModel.deleteSelected(selectedFurnitureId)

                        noteList.forEachIndexed { index, furniture ->
                            if (furniture.id == selectedFurnitureId) {
                                val newList = ArrayList(noteList)
                                Log.i("removing", furniture.id)
                                newList.removeAt(index)
                                _noteList.value = newList


                                homeScreenViewModel.deselectFurniture()
                            }

                        }

                        homeScreenViewModel.updateItem(selectedFurnitureId, null)
                    }) {
                    Icon(Icons.Filled.Delete, contentDescription = "delete furniture")
                }
            }

        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = isEditingFurniture,
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

                /*FilledIconButton(
                    colors = IconButtonDefaults.filledIconButtonColors(
                        contentColor =  MaterialTheme.colorScheme.primary,
                        containerColor =  MaterialTheme.colorScheme.primaryContainer,
                    ),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .size(60.dp),
                    onClick = {isEditingFurniture = false}) {
                    Icon(Icons.Filled.Done,contentDescription = "Close")
                }
                FilledIconButton(
                    colors = IconButtonDefaults.filledIconButtonColors(
                        contentColor =  MaterialTheme.colorScheme.error,
                        containerColor =  MaterialTheme.colorScheme.onError,
                    ),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .size(60.dp),
                    onClick = {isEditingFurniture = false}) {
                    Icon(Icons.Filled.Clear,contentDescription = "Close")
                }*/
                Card(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
                            .width(100.dp)
                    ) {
                        furniture.forEach { furnitureId ->
                            Image(
                                modifier = Modifier.clickable {
                                    val newList = ArrayList(noteList)

                                    newList.add(
                                        MovableObject(
                                            UUID.randomUUID().toString(),
                                            furnitureId
                                        )
                                    )
                                    val id = UUID.randomUUID().toString()
                                    homeScreenViewModel.updateItem(id, MovableObject( id = id, res =  furnitureId,0F,0F))

                                    Log.i("id", newList.last().id)
                                    _noteList.value = newList
                                    isEditingFurniture = false

                                },
                                painter = painterResource(id = furnitureId), contentDescription = ""
                            )
                        }
                    }

                }

            }

        }



        if (!isEditing) {
            person1.Draw()
        }

    }


}

@Composable
fun BackgroundEditor(isEditing: Boolean) {

}

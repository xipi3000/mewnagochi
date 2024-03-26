package com.projecte.mewnagochi.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.LabeledIcon
import com.projecte.mewnagochi.MyViewModel
import com.projecte.mewnagochi.R


@Composable
fun MainScreen(
    myViewModel : MyViewModel = viewModel()
) {
    val selectedItem by myViewModel.navigationBarSelected.collectAsState()
    val navigationBarItems = listOf(LabeledIcon("Home",Icons.Filled.Home),
        LabeledIcon("Activities",  ImageVector.vectorResource(id = R.drawable.baseline_directions_run_24)),
            LabeledIcon("Chats",ImageVector.vectorResource(id = R.drawable.baseline_forum_24))
            )
    Scaffold(
        bottomBar = {

                NavigationBar {
                    navigationBarItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selectedItem == index,
                            onClick = { myViewModel.setNewSelected(index) }
                        )
                    }



            }
        }
    ){
        scaffoldPadding ->
        Column(
            modifier = Modifier.padding(scaffoldPadding)
        ) {
            when (selectedItem) {
                0 -> {
                    HomeScreen()
                }
                1 -> {
                    ActivitiesScreen()
                }
                else -> {
                    ChatScreen()
                }
            }
        }

    }
}

@Composable
fun HomeScreen(){
    Text(text = "HOME")
}
@Composable
fun ActivitiesScreen(){
    Text(text = "ACTIVITIES")
}
@Composable
fun ChatScreen(){
    Text(text = "CHAT")
}

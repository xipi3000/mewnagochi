package com.projecte.mewnagochi.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projecte.mewnagochi.LabeledIcon
import com.projecte.mewnagochi.MyViewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.theme.Person
import kotlinx.coroutines.selects.select

val homeScreen: @Composable () -> Unit = {
    Person().Draw()
}


@Composable
fun MainScreen(
    myViewModel : MyViewModel = viewModel(),
    navController : NavHostController = rememberNavController(),
    navigationBarItems : List<LabeledIcon> =listOf(
        LabeledIcon("Home", Icons.Filled.Home){ Person().Draw()},
        LabeledIcon("Activities",  ImageVector.vectorResource(id = R.drawable.baseline_directions_run_24)) { ActivitiesScreen() },
        LabeledIcon("Chats",ImageVector.vectorResource(id = R.drawable.baseline_forum_24)) { ChatScreen() },
    )
) {
    @Composable
    fun MyBox() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue)
        )
    }

// Define a variable of type @Composable () -> Unit
    var myComposable: @Composable () -> Unit = { }

// Assign your Box composable to the variable
    myComposable = { MyBox() }

    val selectedItem by myViewModel.navigationBarSelected.collectAsState()

    Scaffold(
        bottomBar = {
                NavigationBar {
                    navigationBarItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selectedItem == index,
                            onClick = {
                                if(selectedItem!=index) {
                                    myViewModel.setNewSelected(index)
                                    navController.navigate(item.label)
                                }
                            }
                        )
                    }
            }
        }
    ){
        scaffoldPadding ->
        Column(
            modifier = Modifier.padding(scaffoldPadding)
        ) {



            NavHost(
                navController = navController,
                startDestination = "Home"
            ) {


                navigationBarItems.forEach { item ->
                    composable(item.label){
                        item.screen()
                    }
                }
                }

            }
        }

    }




@Composable
fun ActivitiesScreen(){
    Text(text = "ACTIVITIES")
}
@Composable
fun ChatScreen(){
    Text(text = "CHAT")
}

package com.projecte.mewnagochi.screens.store


import android.widget.Toast
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.services.storage.Item


@Composable
fun StoreScreen(
    storeViewModel: StoreScreenViewModel = viewModel(),
) {
    val items by storeViewModel.items.collectAsState()
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
    ) {
        items(1){
            Button(onClick = storeViewModel::addMoney) {
                Text(text = "+10")
            }
        }
        items(items) {
            item ->
            Spacer(modifier = Modifier.size(30.dp))
            StoreItem(item, storeViewModel::addItem)
            
        }
    }
}

@Composable
fun StoreItem(
    item: StoreItem,
    onClick: (Item,Long,(Throwable?) -> Unit) -> Unit
) {
    if (item.isNew) NewStoreItemContainer(item = item, onClick =  onClick)
    else StoreItemContainer(item = item, onClick =  onClick)
}

@Composable
fun StoreItemContainer(
    onClick: (Item,Long,(Throwable?) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    item: StoreItem,
    colors: CardColors = CardDefaults.outlinedCardColors(
        containerColor = Color.White,
        contentColor = Color.Black
    ),
    border: BorderStroke = BorderStroke(1.dp, Color.Black),
    storeViewModel: StoreScreenViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val userViewModel: UserViewModel = viewModel()
    OutlinedCard(
        modifier = modifier
            .wrapContentSize(),
        colors = colors,
        border = border
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 20.dp)
                .wrapContentSize(Alignment.Center)

        ) {
            Text(
                text = item.name,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,

                )
            Item(modifier = Modifier.padding(10.dp), res = item.id)

            if (item.isPurchased) {
                Text(
                    text = "Already bought",
                    fontSize = 25.sp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(id = R.drawable.coins), contentDescription = "Coins",Modifier.size(60.dp))
                    Text(text = item.cost.toString(),
                        style = MaterialTheme.typography.headlineLarge)
                }
                val context = LocalContext.current
                PucharseButton(onClick = { onClick(item.fromStoreItemToItem(),item.cost){

                    Toast.makeText(context,it?.message, Toast.LENGTH_SHORT).show()
                } }
                ) //{

                    //storeViewModel.buyItem(item.id)
                    //userViewModel.addItem(UserItem(item.id, item.name))
                //}
            }


        }
    }
}


@Composable
fun NewStoreItemContainer(
    onClick: (Item,Long,(Throwable?) -> Unit) -> Unit ,
    item: StoreItem,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
    ) {

        val (storeItem, descriptionText) = createRefs()

        StoreItemContainer(
            onClick = onClick,
            //border = BorderStroke(6.dp, borderGradient),
            modifier = Modifier
                .constrainAs(storeItem) {
                    top.linkTo(parent.top)


                }
                .animatedBorder(
                    borderColors = listOf(Color.Magenta, Color.Cyan),
                    backgroundColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    borderWidth = 7.dp
                ),
            item = item
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Red,
                contentColor = Color.White
            ),
            modifier = Modifier.constrainAs(descriptionText) {
                top.linkTo(storeItem.bottom, margin = (-13).dp)
                start.linkTo(storeItem.start)
                end.linkTo(storeItem.end)
            }
        )
        {
            Text(
                text = "NEW",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(5.dp)
            )

        }
    }
}


@Composable
fun Item(
    modifier: Modifier = Modifier,
    res: Int
) {
    Card(
        modifier = modifier.padding(bottom = 10.dp, top = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )

    ) {
        Image(

            modifier = Modifier
                .padding(10.dp)
                .size(120.dp),
            painter = painterResource(id = res), contentDescription = "Window"
        )
    }
}

@Composable
fun PucharseButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        enabled = enabled,

        modifier = modifier,
        onClick = onClick

    ) {
        Text(
            text = stringResource(R.string.purchase),
            fontSize = 20.sp
        )
    }
}

@Composable
fun Modifier.animatedBorder(
    borderColors: List<Color>,
    backgroundColor: Color,
    shape: Shape = RectangleShape,
    borderWidth: Dp = 1.dp,
    animationDurationInMillis: Int = 2000,
    easing: Easing = LinearEasing
): Modifier {
    val brush = Brush.horizontalGradient(borderColors)
    val infiniteTransition = rememberInfiniteTransition(label = "animatedBorder")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationInMillis, easing = easing),
            repeatMode = RepeatMode.Restart
        ), label = "angleAnimation"
    )

    return this
        .clip(shape)
        .padding(borderWidth)
        .drawWithContent {
            rotate(angle) {
                drawCircle(
                    brush = brush,
                    radius = size.width,
                    blendMode = BlendMode.SrcIn,
                )
            }
            drawContent()
        }
        .background(color = backgroundColor, shape = shape)
}


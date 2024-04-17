package com.projecte.mewnagochi.ui.theme.store

import android.graphics.fonts.FontStyle
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout

import com.projecte.mewnagochi.R

@Composable
fun StoreScreen(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        NewStoreItem(itemName = "FINESTRA",modifier = Modifier.padding(30.dp))
        StoreItem(itemName = "FINESTRA", modifier = Modifier.padding(30.dp))
        StoreItem(itemName = "FINESTRA", modifier = Modifier.padding(30.dp))
    }

}

@Composable
fun StoreItem(modifier: Modifier = Modifier,
              itemName: String,
              colors: CardColors = CardDefaults.outlinedCardColors(
                  containerColor = Color.White,
                  contentColor = Color.Black
              ),
              border: BorderStroke = BorderStroke(1.dp, Color.Black)
              ) {

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
                    text = itemName,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,

                    )
                Item(modifier = Modifier.padding(10.dp))
                PucharseButton()


            }
        }


}

@Composable
fun NewStoreItem(
    itemName: String,
    modifier: Modifier = Modifier
    ) {
        ConstraintLayout(
            modifier = modifier
        ) {

            val (storeItem, descriptionText) = createRefs()

            StoreItem(
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
                    )

                ,
                itemName = itemName
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
fun Item(modifier: Modifier = Modifier){
    Card(
        modifier = modifier.padding(bottom = 10.dp, top = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )

    ) {
        Image(
            modifier = Modifier.padding(10.dp),
            painter = painterResource(id = R.drawable.window), contentDescription = "Window"
        )
    }
}
@Composable
fun PucharseButton(modifier: Modifier = Modifier){
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        modifier = modifier,
        onClick = { /*TODO*/ }) {
        Text(text = stringResource(R.string.purchase),
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


@Preview
@Composable
fun preview() {
    StoreItem(itemName = "FINESTRA")
}

@Preview
@Composable
fun preview2() {
    NewStoreItem("FINESTRA")
}

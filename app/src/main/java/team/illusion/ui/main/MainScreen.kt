package team.illusion.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import team.illusion.R
import team.illusion.data.model.Center
import team.illusion.ui.theme.IllusionColor
import team.illusion.ui.theme.LocalUseTablet
import team.illusion.ui.theme.LocalUseVertical
import team.illusion.ui.theme.Team1llusionTheme

@Composable
fun MainScreen(identifier: String, center: Center = Center.None, event: (MainEvent) -> Unit) {


    val state = rememberLazyGridState()
    val isTablet = LocalUseTablet.current
    val isVertical = LocalUseVertical.current

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(color = IllusionColor.IllusionYellow, shape = RoundedCornerShape(size = 10.dp))
                .padding(vertical = 8.dp, horizontal = 16.dp),
            text = center.centerName,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { event(MainEvent.Logout) }
                .padding(16.dp),
            text = "logout"
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalArrangement = if (isVertical) Arrangement.Center else Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { event(MainEvent.ClickAdmin) }) {
                    Image(
                        modifier = Modifier
                            .size(if (isTablet && isVertical) 200.dp else 100.dp)
                            .clip(CircleShape),
                        painter = painterResource(id = R.mipmap.logo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "회원번호를 입력후\n확인 버튼을 눌러주세요.",
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isTablet && isVertical) 36.sp else 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(40.dp))
                Box(
                    modifier = Modifier
                        .width(if (isTablet) 400.dp else 300.dp)
                        .border(
                            width = 2.dp,
                            shape = RoundedCornerShape(12.dp),
                            color = IllusionColor.IllusionYellow
                        )
                        .padding(vertical = if (isTablet) 20.dp else 10.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = identifier,
                        color = IllusionColor.IllusionYellow,
                        fontSize = if (isTablet) 40.sp else 20.sp,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                if (isVertical) {
                    Keypad(modifier = Modifier, state = state) { numberPad ->
                        when (numberPad) {
                            Inputs.DELETE -> event(MainEvent.Delete)
                            Inputs.CONFIRM -> event(MainEvent.Confirm)
                            else -> event(MainEvent.InputId(numberPad.number))
                        }
                    }
                }
            }
            if (!isVertical) {
                Keypad(modifier = Modifier, state = state) { numberPad ->
                    when (numberPad) {
                        Inputs.DELETE -> event(MainEvent.Delete)
                        Inputs.CONFIRM -> event(MainEvent.Confirm)
                        else -> event(MainEvent.InputId(numberPad.number))
                    }
                }
            }
        }
    }
}

@Composable
fun Keypad(
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    onClick: (Inputs) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier.width(300.dp),
        state = state,
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = Inputs.values()) { item ->
            Number(
                number = item,
                onClick = { onClick(item) }
            )
        }
    }
}

@Composable
private fun Number(number: Inputs, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(90.dp)
            .clickable(onClick = onClick)
            .background(color = number.color, shape = CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.number,
            fontWeight = FontWeight.Bold,
            fontSize = number.fontSize,
            textAlign = TextAlign.Center
        )
    }
}

enum class Inputs(
    val number: String,
    val fontSize: TextUnit = 34.sp,
    val color: Color = IllusionColor.DefaultGray,
) {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    DELETE("DEL", 20.sp),
    ZERO("0"),
    CONFIRM("OK", 20.sp, IllusionColor.IllusionYellow)
}

@Preview(showBackground = true)
@Composable
fun KeypadPreview() {
    Keypad {}
}

@Preview(showBackground = true, device = Devices.TABLET)
@Composable
fun KeypadTabletPreview() {
    Keypad {}
}


@Preview(showBackground = true, device = Devices.TABLET)
@Composable
fun DefaultTabletPreview() {
    Team1llusionTheme {
        MainScreen(identifier = "uiState.memberIdentifier") {}
    }
}

@Preview(showBackground = true, widthDp = 720, heightDp = 1080)
@Composable
fun DefaultTabletPreview2() {
    Team1llusionTheme {
        MainScreen("uiState.memberIdentifier") {}
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Team1llusionTheme {
        MainScreen("uiState.memberIdentifier") {}
    }
}

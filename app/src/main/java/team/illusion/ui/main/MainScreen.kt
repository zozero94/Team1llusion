package team.illusion.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import team.illusion.R
import team.illusion.ui.theme.Team1llusionTheme

@Composable
fun MainScreen(identifier: String, event: (MainEvent) -> Unit) {
    val configuration = LocalConfiguration.current
    val modifier = when {
        configuration.screenWidthDp > 600 -> Modifier.width(400.dp) // 패드 크기일 경우 600.dp로 제한
        else -> Modifier.fillMaxWidth() // 휴대폰 크기일 경우 가로로 꽉 채움
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    indication = rememberRipple(color = Color.Gray),
                    interactionSource = remember { MutableInteractionSource() },
                ) { event(MainEvent.Logout) }
                .padding(16.dp),
            text = "logout"
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { event(MainEvent.ClickAdmin) }) {
                Image(
                    modifier = Modifier
                        .size(200.dp)
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
                fontSize = 36.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .width(440.dp)
                    .border(
                        width = 2.dp,
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xffFBC02D)
                    )
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = identifier, color = Color(0xffFBC02D), fontSize = 40.sp)
            }
            Spacer(modifier = Modifier.height(40.dp))
            Keypad(modifier = modifier) { numberPad ->
                when (numberPad) {
                    NumberPad.DELETE -> event(MainEvent.Delete)
                    NumberPad.CONFIRM -> event(MainEvent.Confirm)
                    else -> event(MainEvent.InputId(numberPad.number))
                }
            }
        }
    }
}

@Composable
fun Keypad(modifier: Modifier = Modifier, onClick: (NumberPad) -> Unit) {

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(items = NumberPad.values()) { index, item ->
            Number(
                number = item,
                color = Color(if (NumberPad.values().lastIndex == index) 0xffFBC02D else 0xffeeeeee),
                onClick = { onClick(item) }
            )
        }
    }
}

@Composable
private fun Number(number: NumberPad, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(90.dp)
            .clickable(onClick = onClick)
            .background(color = color, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.number,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            textAlign = TextAlign.Center
        )
    }
}

enum class NumberPad(val number: String) {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    DELETE("<"),
    ZERO("0"),
    CONFIRM("확인")
}

@Preview(showBackground = true)
@Composable
fun KeypadPreview() {
    Keypad() {}
}

@Preview(showBackground = true, device = Devices.TABLET)
@Composable
fun KeypadTabletPreview() {
    Keypad() {}
}


@Preview(showBackground = true, device = Devices.TABLET)
@Composable
fun DefaultTabletPreview() {
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

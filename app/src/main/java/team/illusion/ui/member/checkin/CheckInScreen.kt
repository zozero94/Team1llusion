package team.illusion.ui.member.checkin

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import team.illusion.MemberPreviewProvider
import team.illusion.data.model.Member

@Composable
fun CheckInScreen(member: Member, deleteCheckIn: (String) -> Unit) {
    val openDialog = rememberSaveable { mutableStateOf<String?>(null) }
    openDialog.value?.let { checkInDate ->
        AlertDialog(
            onDismissRequest = { openDialog.value = null },
            title = { Text(text = "\"$checkInDate\" 체크인을 취소합니다") },
            confirmButton = {
                TextButton(onClick = {
                    deleteCheckIn(checkInDate)
                    openDialog.value = null
                }) {
                    Text(text = stringResource(id = R.string.ok), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = null }) {
                    Text(text = stringResource(id = R.string.cancel), color = Color.White)
                }
            },
        )
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (member.checkInDate.isEmpty()) {
            item {
                Text(modifier = Modifier.fillMaxWidth(), text = "데이터 없음", textAlign = TextAlign.Center)
            }
        } else {
            items(member.checkInDate) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = it)
                    IconButton(onClick = { openDialog.value = it }) {
                        Image(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview(
    @PreviewParameter(MemberPreviewProvider::class) member: Member
) {
    CheckInScreen(member = member, deleteCheckIn = {})
}
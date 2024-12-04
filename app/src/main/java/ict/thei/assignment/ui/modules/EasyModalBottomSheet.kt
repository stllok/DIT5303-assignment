package ict.thei.assignment.ui.modules

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState


// Example from https://developer.android.com/develop/ui/compose/components/bottom-sheets?hl=zh-tw
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasyModalBottomSheet(showBottomSheet: MutableState<Boolean>, content: @Composable () -> Unit) {
    val state = rememberModalBottomSheetState()
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = state
        ) {
            content()
        }
    }
}

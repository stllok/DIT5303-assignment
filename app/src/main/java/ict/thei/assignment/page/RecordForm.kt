package ict.thei.assignment.page

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ict.thei.assignment.ui.modules.DatePickerModal

class RecordForm : ComponentActivity() {
    // State
    var datePickerMutex = mutableStateOf(false)
    var timePickerMutex = mutableStateOf(false)

    // Data mutex
    var titleMutex = mutableStateOf("")
    var descriptionMutex = mutableStateOf("")
    var amountMutex = mutableIntStateOf(0)
    var categoryIdxMutex = mutableIntStateOf(-1)
    var accountIdxMutex = mutableIntStateOf(-1)
    var dateMutex = mutableStateOf("")
    var timeMutex = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            page()
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun page() {
        var title by remember { titleMutex }
        var description by remember { descriptionMutex }
        var amount by remember {amountMutex}
        var categoryIdx by remember { categoryIdxMutex }
        var accountIdx by remember { accountIdxMutex}
        var date by remember {dateMutex}
        var time by remember {timeMutex}
        var datePicker by remember {datePickerMutex}
        var timePicker by remember {timePickerMutex}

        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(
                title = { Text("Record form") },
                navigationIcon = {
                    IconButton(onClick = {
                        finish()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
            )
        },
            bottomBar = {
                BottomAppBar(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                        Text("Save & Apply")
                    }
                }
            }) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(horizontal = 10.dp)
            ) {
                TextField(
                    value = title,
                    label = { Text("Title") },
                    onValueChange = { title = it},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
                HorizontalDivider(thickness = 4.dp, modifier = Modifier.padding(vertical = 10.dp))
                OutlinedTextField(
                    value = description,
                    label = { Text("Description") },
                    onValueChange = {description = it},
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton (onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Category: Not selected")
                }
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Account: Not selected")
                }
                OutlinedTextField(
                    value = amount.toString(),
                    onValueChange = { amount = it.toInt()},
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
                    OutlinedButton(onClick = {}) {
                        Text("Date: Not selected")
                    }
                    OutlinedButton(onClick = {}) {
                        Text("Time: Not selected")
                    }
                }
                if (datePicker) {
                    DatePickerModal(onDateSelected = {}, onDismiss = {})
                }
            }
        }
    }

    @Preview
    @Composable
    private fun preview() {
        page()
    }
}


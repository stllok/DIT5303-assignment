package ict.thei.assignment.page

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ict.thei.assignment.db.AppDatabase
import ict.thei.assignment.db.Record
import ict.thei.assignment.db.RecordInsert
import ict.thei.assignment.ui.modules.DatePickerDialog
import ict.thei.assignment.ui.modules.DialPickerDialog
import ict.thei.assignment.ui.modules.EasyModalBottomSheet
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
class RecordForm : ComponentActivity() {
    val db by lazy { AppDatabase.newInstance(applicationContext) }
    val recordDb by lazy { db.recordDao() }

    // exists record, if not null then it's update/delete action, else it's create new record
    val record by lazy {
        intent.extras?.getLong("RecordIdx")?.let {
            recordDb.get(it)
        }
    }

    // State
    var datePickerMutex = mutableStateOf(false)
    var timePickerMutex = mutableStateOf(false)
    var categoryPickerMutex = mutableStateOf(false)
    var accountPickerMutex = mutableStateOf(false)
    var deleteConfirmMutex = mutableStateOf(false)
    var validMutex = mutableStateOf(false)

    // Data mutex
    var titleMutex = mutableStateOf("")
    var descriptionMutex = mutableStateOf("")
    var amountMutex = mutableStateOf("0")
    var categoryIdxMutex = mutableIntStateOf(0)
    var accountIdxMutex = mutableIntStateOf(-1)
    var dateMutex = mutableLongStateOf(Timestamp(System.currentTimeMillis()).time)
    var timeHourMutex = mutableIntStateOf(LocalDateTime.now().hour)
    var timeMinutesMutex = mutableIntStateOf(LocalDateTime.now().minute)

    val accountDb by lazy { db.accountDao() }
    val categoryDb by lazy { db.categoryDao() }

    val categories by lazy { categoryDb.getAll() }
    val accounts by lazy { accountDb.getAll() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set data if record exists
        record?.let {
            setByExistsData(it)
        }

        setContent {
            Page()
        }
    }

    private fun setByExistsData(o: Record) {
        titleMutex.value = o.name
        descriptionMutex.value = o.description
        amountMutex.value = o.amount.toString()
        categoryIdxMutex.intValue = o.categoryId.toInt() - 1
        accountIdxMutex.intValue = o.accountId.toInt() - 1

        val c = Calendar.getInstance()
        c.time = Date(o.recordAt)

        dateMutex.longValue = c.timeInMillis
        timeHourMutex.intValue = c.get(Calendar.HOUR_OF_DAY)
        timeMinutesMutex.intValue = c.get(Calendar.MINUTE)
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Page() {
        var title by remember { titleMutex }
        var description by remember { descriptionMutex }
        var amount by remember { amountMutex }
        var categoryIdx by remember { categoryIdxMutex }
        var accountIdx by remember { accountIdxMutex }
        var date by remember { dateMutex }
        var timeHour by remember { timeHourMutex }
        var timeMinutes by remember { timeMinutesMutex }

        // bool
        var datePicker by remember { datePickerMutex }
        var timePicker by remember { timePickerMutex }
        var categoryPicker by remember { categoryPickerMutex }
        var accountPicker by remember { accountPickerMutex }
        var deleteConfirm by remember { deleteConfirmMutex }

        var valid by remember { validMutex }

        val validator = {
            title.isNotEmpty() && "^[1-9-]\\d*\$".toRegex()
                .containsMatchIn(amount) && categoryIdx != -1 && accountIdx != -1
        }

        valid = validator()
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
                actions = {
                    record?.let {
                        IconButton(onClick = {
                            deleteConfirm = true
                        }) {
                            Icon(Icons.Filled.Delete, null)
                        }
                    }
                }
            )
        },
            bottomBar = {
                BottomAppBar(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = {
                        val c = Calendar.getInstance()
                        c.time = Date(date)
                        c.set(Calendar.HOUR_OF_DAY, timeHour)
                        c.set(Calendar.MINUTE, timeMinutes)

                        record?.let {
                            // Do update when record exists
                            recordDb.update(
                                it.copy(
                                    name = title,
                                    description = description,
                                    amount = amount.trim('0').toLong(),
                                    accountId = accounts[accountIdx].id,
                                    categoryId = categories[categoryIdx].id,
                                    recordAt = c.timeInMillis
                                )
                            )
                        } ?: {
                            // Insert if record not exists
                            recordDb.insertAll(
                                RecordInsert(
                                    name = title,
                                    description = description,
                                    amount = amount.trim('0').toLong(),
                                    accountId = accounts[accountIdx].id,
                                    categoryId = categories[categoryIdx].id,
                                    recordAt = c.timeInMillis
                                )
                            )
                        }()

                        finish()
                    }, modifier = Modifier.fillMaxWidth(), enabled = valid) {
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
                    onValueChange = { title = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
                HorizontalDivider(thickness = 4.dp, modifier = Modifier.padding(vertical = 10.dp))
                OutlinedTextField(
                    value = description,
                    label = { Text("Description") },
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = { categoryPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Category: ${
                            if (categoryIdx == -1) {
                                "Not selected"
                            } else {
                                categories[categoryIdx].name
                            }
                        }"
                    )
                }
                OutlinedButton(
                    onClick = { accountPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Account: ${
                            if (accountIdx == -1) {
                                "Not selected"
                            } else {
                                accounts[accountIdx].name
                            }
                        }"
                    )
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    OutlinedButton(onClick = { datePicker = true }) {
                        Text("Date: ${SimpleDateFormat("yyyy-MM-dd").format(Date(date))}")
                    }
                    OutlinedButton(onClick = { timePicker = true }) {
                        Text("Time: $timeHour:$timeMinutes")
                    }
                }
                if (datePicker) {
                    DatePickerDialog(onDateSelected = {
                        it?.let { date = it }
                    }, onDismiss = { datePicker = false })
                }
                if (timePicker) {
                    DialPickerDialog(onConfirm = { time ->
                        timeHour = time.hour
                        timeMinutes = time.minute
                    }, onDismiss = { timePicker = false })
                }
                if (categoryPicker) {
                    CategoryMoral()
                }
                if (accountPicker) {
                    AccountMoral()
                }
                if (deleteConfirm) {
                    deleteConfirmAlert()
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun deleteConfirmAlert() {
        var deleteConfirm by remember { deleteConfirmMutex }
        AlertDialog(
            icon = {
                Icon(Icons.Filled.Delete, contentDescription = "Example Icon")
            },
            title = {
                Text("Delete confirm")
            },
            text = {
                Text("Are you sure to delete the record, the action is not undoable.")
            },
            onDismissRequest = {
                deleteConfirm = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        recordDb.delete(record!!)
                        finish()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deleteConfirm = false;
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun CategoryMoral() {
        var categoryIdx by remember { categoryIdxMutex }
        var categoryPicker by remember { categoryPickerMutex }
        EasyModalBottomSheet(categoryPickerMutex, rememberModalBottomSheetState()) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("Category", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 10.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    categories.forEachIndexed { i, it ->
                        Button(onClick = {
                            categoryIdx = i
                            categoryPicker = false
                        }) {
                            Text(it.name)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AccountMoral() {
        var accountIdx by remember { accountIdxMutex }
        var accountPicker by remember { accountPickerMutex }
        EasyModalBottomSheet(accountPickerMutex, rememberModalBottomSheetState()) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text("Category", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 10.dp))
                accounts.forEachIndexed { i, it ->
                    Button(
                        onClick = {
                            accountIdx = i
                            accountPicker = false
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                    ) {
                        Text("${it.name}\t\t$${accountDb.totalBalance(it.id)}")
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun Preview() {
        Page()
    }
}


package ict.thei.assignment.page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import ict.thei.assignment.db.AccountInsert
import ict.thei.assignment.db.AppDatabase
import ict.thei.assignment.db.RecordInsert
import ict.thei.assignment.db.UserConfigInsert
import ict.thei.assignment.ui.theme.AssignmentTheme

class InitialSetupPage : ComponentActivity() {
    // DB
    val db by lazy { AppDatabase.newInstance(applicationContext) }
    val currency by lazy { db.currencyDao().getAll() }

    // State
    val stage_idx_mutex = mutableIntStateOf(1)

    //    Step 1
    var account_name_mutex = mutableStateOf("")
    var account_balance_mutex = mutableStateOf("")
    var currency_idx_mutex = mutableIntStateOf(-1)

    // Step 2
    var password_mutex = mutableStateOf("")

    // Step 3
    val category by lazy {
        db.categoryDao().getAll()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssignmentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val stageIdx by remember { stage_idx_mutex }
                    Box(Modifier.padding(innerPadding)) {
                        when (stageIdx) {
                            1 -> Step1()
                            2 -> Step2()
                            3 -> Step3()
                            4 -> {
                                db.userConfigDao().initialize(
                                    UserConfigInsert(
                                        password = password_mutex.value
                                    )
                                )
                                db.accountDao().insertAll(
                                    AccountInsert(
                                        name = account_name_mutex.value,
                                        currencyId = currency[currency_idx_mutex.intValue].id
                                    )
                                )
                                db.recordDao().insertAll(
                                    RecordInsert(
                                        "Initial setup",
                                        "",
                                        account_balance_mutex.value.toLong(),
                                        db.accountDao().get(1)!!.id,
                                        db.categoryDao().get(1)!!.id
                                    )
                                )

                                Intent(
                                    this@InitialSetupPage,
                                    MainActivity::class.java
                                ).also { startActivity(it) }
                            }

                            else -> throw Exception("UNREACHABLE")
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Preview(showBackground = true)
    @Composable
    private fun Step1() {
        var expanded by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false,
        )
        var valid by remember { mutableStateOf(false) }
        var account_name by remember { account_name_mutex }
        var account_balance by remember { account_balance_mutex }
        var currency_idx by remember { currency_idx_mutex }
        var stage_idx by remember { stage_idx_mutex }

        val validator = {
            valid = account_name.isNotEmpty() && account_balance.ifBlank { "-1" }
                .toInt() >= 0 && currency_idx != -1
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            Row {
                Text(
                    "Setup account",
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                )
            }
            Row {
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            }
            Row {
                Text(
                    "Before use the application you have to setup your account first.",
                    color = Color.Gray
                )
            }
            Row {
                OutlinedTextField(
                    value = account_name,
                    onValueChange = {
                        account_name = it
                        validator()
                    },
                    label = { Text("Account Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Account Balance") },
                    value = account_balance,
                    onValueChange = {
                        account_balance = it
                        validator()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Row {
                TextButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Currency: (${
                            if (currency_idx != -1) {
                                currency[currency_idx].name
                            } else {
                                "Not selected"
                            }
                        })"
                    )
                }

                if (expanded) {
                    ModalBottomSheet(
                        modifier = Modifier.fillMaxHeight(),
                        sheetState = sheetState,
                        onDismissRequest = { expanded = false }
                    ) {
                        currency.forEachIndexed { i, it ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .padding(15.dp)
                                    .clickable {
                                        Log.d("DATABASE", "Loading $it ($i)")
                                        currency_idx = i
                                        expanded = false
                                        validator()
                                    }, horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(it.abbreviation)
                                Text(it.name)
                            }
                        }
                    }
                }
            }

            Row {
                Button(enabled = valid, onClick = {
                    stage_idx++
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Next")
                }
            }


        }

    }

    @Preview(showBackground = true)
    @Composable
    private fun Step2() {
        var password by remember { password_mutex }
        var validate by remember { mutableStateOf(false) }
        var stage by remember { stage_idx_mutex }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }

        val validator = {
            validate = password.isNotEmpty()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            Row {
                Text(
                    "Setup password",
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                )
            }
            Row {
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            }
            Row {
                Text(
                    "For security concert, it is recommended to setup password in order to limit the access of the application. You can skip it for now and set it up later.",
                    color = Color.Gray
                )
            }
            Row {
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        validator()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        // Please provide localized description for accessibility services
                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = {passwordVisible = !passwordVisible}){
                            Icon(imageVector  = image, description)
                        }
                    })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(onClick = {
                    password = ""
                    stage++
                }) { Text("Skip") }
                Button(onClick = {
                    stage++
                }, enabled = validate) { Text("Next") }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun Step3() {
        var stage by remember { stage_idx_mutex }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            Row {
                Text(
                    "Setup category",
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                )
            }
            Row {
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            }
            Row {
                LazyColumn {
                    category.forEach {
                        item {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(it.name)
                                IconButton(onClick = {}) {
                                    Icon(Icons.Filled.Close, contentDescription = "Close")
                                }
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(onClick = {
                    stage++
                }) { Text("Next") }
            }
        }
    }
}
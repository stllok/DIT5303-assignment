package ict.thei.assignment.page

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ict.thei.assignment.db.AppDatabase
import ict.thei.assignment.db.Record
import kotlinx.coroutines.launch
import java.text.DateFormat.getDateInstance
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPage(modifier: Modifier, appDb: AppDatabase) {
    val recordDb = appDb.recordDao()
    val accountDb = appDb.accountDao()
    val currencyDb = appDb.currencyDao()
    var records = recordDb.getAll()
    var stateDate = ""
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(200.dp)) {
                Text(
                    "WalletTracker",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Report")
                }
                TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Settings")
                }
            }
        },
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            val context = LocalContext.current
            TopAppBar(
                title = { Text("Record", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { drawerState.apply { scope.launch { if (isClosed) open() else close() } } }) {
                        Icon(Icons.Filled.Menu, null)
                    }
                },

                actions = {
                    IconButton(
                        onClick = {
                            Intent(
                                context,
                                RecordForm::class.java
                            ).also { context.startActivity(it) }
                        } //do something
                    ) {
                        Icon(Icons.Filled.Add, null)
                    }
                }
            )
        }) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                records.forEach {
                    val account = accountDb.get(it.accountId)
                    val currency = currencyDb.get(account!!.currencyId)
                    var tmpDate = getDateInstance().format(Date(it.recordAt))

                    if (tmpDate != stateDate) {
                        stateDate = tmpDate
                        Text(
                            tmpDate,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    }
                    RecordCard(it, account.name, currency!!.symbol)
                }
            }
        }
    }
}


@Composable
private fun RecordCard(record: Record, accountName: String, currencySynbol: String) {
    val content = LocalContext.current
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 5.dp),
        onClick = {
            val intent = Intent(content, RecordForm::class.java)
            intent.putExtra("RecordIdx", record.id)
            content.startActivity(intent)
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                accountName,
                color = Color.White,
                modifier = Modifier.padding(vertical = 10.dp),
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(record.name)
            Text(
                "$currencySynbol${record.amount}", color = if (record.amount > 0) {
                    Color(red = 0x00, green = 0x64, blue = 0x00)
                } else {
                    Color.Red
                }
            )
        }
    }
}


@Preview(showBackground = false)
@Composable
private fun PreviewRecord() {
    RecordCard(
        record = Record(1, "Preview record", "", 232, 1, 1, 1),
        accountName = "Preview account",
        currencySynbol = "$"
    )
}

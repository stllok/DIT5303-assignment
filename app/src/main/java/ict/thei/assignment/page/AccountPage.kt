package ict.thei.assignment.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ict.thei.assignment.db.Account
import ict.thei.assignment.db.AppDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPage(modifier: Modifier, appDb: AppDatabase) {
    val accountDb = appDb.accountDao()
    val currencyDb = appDb.currencyDao()

    val accounts = accountDb.getAll()

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = { Text("Account", fontWeight = FontWeight.Bold) },
            navigationIcon = {
//                IconButton(onClick = {}) {
//                    Icon(Icons.Filled.Menu, null)
//                }
            }
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 10.dp)
        ) {
            accounts.forEach {
                val currency = currencyDb.get(it.currencyId)!!
                AccountCard(
                    it,
                    accountDb.totalBalance(it.id),
                    currency.symbol,
                    currency.abbreviation
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountCard(
    record: Account,
    totalBalance: Long,
    currencySymbol: String,
    currencyAbbreviation: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(record.name, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Delete, "Close")
                }
            }
            Text(
                "$currencySymbol$totalBalance $currencyAbbreviation",
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAccount() {
    AccountCard(Account(1, "Preview account", false, 1), 150, "$", "HKD")
}

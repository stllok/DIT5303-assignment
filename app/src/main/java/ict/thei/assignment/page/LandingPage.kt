package ict.thei.assignment.page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import ict.thei.assignment.db.AppDatabase
import ict.thei.assignment.db.CategoryInsert
import ict.thei.assignment.db.CurrencyInsert
import ict.thei.assignment.ui.theme.AssignmentTheme

class LandingPage : ComponentActivity() {
    val db by lazy { AppDatabase.newInstance(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AssignmentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        MainField(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }


        if (db.accountDao().getAll().isEmpty()) {
            inializeSetup()
        } else {
            jumpToMain()
        }
    }

    private fun inializeSetup() {
        val currencyDb = db.currencyDao()
        currencyDb.insertAll(
            CurrencyInsert(
                name = "United States dollar",
                symbol = "$",
                abbreviation = "USD"
            ),
            CurrencyInsert(
                name = "Hong Kong dollar",
                symbol = "$",
                abbreviation = "HKD"
            )
        )

        val currencys = currencyDb.getAll()
        Log.d("DATABASE", "getAll(): $currencys")

        val currency = currencyDb.get(2)
        Log.d("DATABASE", "get(2): $currency")

        val categoryDb = db.categoryDao()

        categoryDb.insertAll(
            CategoryInsert(
                name = "Unclassified",
            ),
            CategoryInsert(
                name = "Food & Drink",
            ),
            CategoryInsert(
                name = "Restaurant"
            ),
            CategoryInsert(name = "Transport"),
            CategoryInsert(name = "Shopping"),
            CategoryInsert(name = "Entertainment"),
            CategoryInsert(name = "Working"),
            CategoryInsert(name = "Social"),
            CategoryInsert(name = "Investment"),
            CategoryInsert(name = "Journal"),
            CategoryInsert(name = "Self Development"),
            CategoryInsert(name = "Health"),
            CategoryInsert(name = "Miscellaneous")
        )

        val categories = categoryDb.getAll()
        Log.d("DATABASE", "getAll(): $categories")

        val category = categoryDb.get(3)
        Log.d("DATABASE", "get(3): $category")

        Intent(
            this@LandingPage,
            InitialSetupPage::class.java
        ).also { startActivity(it) }
    }

    private fun jumpToMain() {
        Intent(
            this@LandingPage,
            MainActivity::class.java
        ).also { startActivity(it) }
    }

    @Composable
    private fun MainField(modifier: Modifier) {
        Column {
            Icon(
                Icons.Default.Wallet,
                contentDescription = "Wallet",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Text(
                "eWallet",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = TextUnit(35f, TextUnitType.Sp)
            )
        }
    }

    @Preview(showBackground = true, device = "id:pixel_5")
    @Composable
    private fun PreviewMainField() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            MainField(Modifier)
        }
    }

}

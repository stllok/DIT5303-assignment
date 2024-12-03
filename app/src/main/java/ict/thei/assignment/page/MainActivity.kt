package ict.thei.assignment.page

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ict.thei.assignment.db.AppDatabase
import ict.thei.assignment.ui.theme.AssignmentTheme

private val TAB_NAME = listOf("Record", "Account")

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val tabIndex = remember { mutableIntStateOf(0) }
            AssignmentTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                    TabSelector(selectedTabIndex = tabIndex, tabs = TAB_NAME)
                }) { innerPadding ->
                    val db = AppDatabase.newInstance(applicationContext)
                    val composes: List<@Composable () -> Unit> by lazy {
                        listOf(
                            {
                                RecordPage(
                                    modifier = Modifier
                                        .padding(
                                            innerPadding
                                        )
                                        .padding(horizontal = 10.dp),
                                    appDb = db
                                )
                            }, {
                                AccountPage(
                                    modifier = Modifier.padding(innerPadding)
                                        .padding(horizontal = 10.dp),
                                    appDb = db
                                )
                            })
                    }
                    composes[tabIndex.intValue]()
                }
            }
        }
    }
}

@Composable
fun TabSelector(tabs: List<String>, selectedTabIndex: MutableState<Int>) {
    NavigationBar {
        tabs.forEachIndexed { index, title ->
            NavigationBarItem(
                selected = selectedTabIndex.value == index,
                onClick = { selectedTabIndex.value = index },
                label = { Text(title, color = Color(0, 0, 0)) },
                icon = {
                    when (index) {
                        0 -> Icons.Filled.Receipt
                        1 -> Icons.Filled.Wallet
                        else -> throw Exception("UNREACHABLE")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleTabLayout() {
    val dummy = remember { mutableIntStateOf(0) }
    TabSelector(tabs = TAB_NAME, selectedTabIndex = dummy)
}
package com.eshan.dicegame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.eshan.dicegame.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(modifier = Modifier.padding(innerPadding), activity = this)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, activity: Activity) {
    var showPopup by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.homepage_bg),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Welcome to the\nDice Game",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        val intent = Intent(activity, GameActivity::class.java)
                        activity.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .padding(8.dp)
                ) {
                    Text(text = "New game", color = Color.Black)
                }

                Button(
                    onClick = { showPopup = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .padding(8.dp)
                ) {
                    Text(text = "About", color = Color.Black)
                }
            }
        }

        if (showPopup) {
            AlertDialog(
                onDismissRequest = { showPopup = false },
                title = { Text(text = "w2052115 / 20221126 - Eshan Wijerathna") },
                text = {
                    Text(
                        text = "I confirm that I understand what plagiarism is and have read and understood the section on Assessment " +
                                "Offences in the Essential Information for Students. " +
                                "The work that I have submitted is entirely my own. " +
                                "Any work from other authors is duly referenced  and acknowledged.\n",
                        color = Color.Black
                    )
                },
                confirmButton = {
                    Button(onClick = { showPopup = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
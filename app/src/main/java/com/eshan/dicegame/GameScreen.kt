package com.eshan.dicegame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun GameScreen() {
    var playerDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    var selectedDice by remember { mutableStateOf(List(5) { false }) }
    var computerDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    var playerScore by remember { mutableStateOf(0) }
    var computerScore by remember { mutableStateOf(0) }
    var rollsLeft by remember { mutableStateOf(3) }
    var isNewRound by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Score Display
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text("Player Score: $playerScore", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Computer Score: $computerScore", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Player's Dice
        Text("Your Dice:", fontSize = 20.sp)
        DiceRow(
            diceValues = playerDice,
            selectedDice = selectedDice,
            isSelectionEnabled = rollsLeft < 3,
            onDiceClick = { index ->
                if (rollsLeft < 3) {
                    selectedDice = selectedDice.toMutableList().also { it[index] = !it[index] }
                }
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Computer's Dice
        Text("Computer's Dice:", fontSize = 20.sp)
        DiceRow(diceValues = computerDice, selectedDice = List(5) { false }, isSelectionEnabled = false)
        Spacer(modifier = Modifier.height(30.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (rollsLeft == 3) {
                        playerScore = 0
                        computerScore = 0
                        isNewRound = true
                    }

                    if (rollsLeft > 0) {
                        // Player Dice Roll
                        playerDice = playerDice.mapIndexed { index, value ->
                            if (rollsLeft == 3 || !selectedDice[index]) Random.nextInt(1, 7) else value
                        }

                        // Computer Dice Roll with Strategy
                        computerDice = computerDice.map {
                            if (rollsLeft == 3 || it < 4) Random.nextInt(1, 7) else it
                        }

                        rollsLeft--

                        if (rollsLeft == 0) {
                            // Auto-score after the last roll
                            updateScore(
                                playerDice,
                                computerDice,
                                { newComputerDice -> computerDice = newComputerDice },
                                { score -> playerScore += score },
                                { score -> computerScore += score }
                            )
                            rollsLeft = 3 // Reset for next round
                            selectedDice = List(5) { false } // Reset selections
                        }
                    }

                },
                shape = RoundedCornerShape(10.dp),
                enabled = rollsLeft > 0
            ) {
                Text(if (rollsLeft == 3) "Throw" else "Re-throw ($rollsLeft left)", fontSize = 18.sp)
            }

            Button(
                onClick = {
                    updateScore(
                        playerDice,
                        computerDice,
                        { newComputerDice -> computerDice = newComputerDice },
                        { score -> playerScore += score },
                        { score -> computerScore += score }
                    )
                    rollsLeft = 3 // Reset for next round
                    selectedDice = List(5) { false } // Reset selections
                },
                shape = RoundedCornerShape(10.dp),
                enabled = rollsLeft < 3
            ) {
                Text("Score", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun DiceRow(diceValues: List<Int>, selectedDice: List<Boolean>, isSelectionEnabled: Boolean, onDiceClick: (Int) -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        diceValues.forEachIndexed { index, value ->
            Box(contentAlignment = Alignment.Center) {
                if (selectedDice[index]) {
                    Canvas(modifier = Modifier.size(90.dp)) {
                        drawRect(
                            color = Color.Green,
                            size = size.copy(width = size.width - 10.dp.toPx(), height = size.height - 10.dp.toPx())
                        )
                    }
                }
                Image(
                    painter = painterResource(id = getDiceImage(value)),
                    contentDescription = "Dice $value",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(10.dp)
                        .clickable(enabled = isSelectionEnabled) { onDiceClick(index) }
                )
            }
        }
    }
}

fun updateScore(
    playerDice: List<Int>,
    computerDice: List<Int>,
    updateComputer: (List<Int>) -> Unit,
    setPlayerScore: (Int) -> Unit,
    setComputerScore: (Int) -> Unit
) {
    val playerTotal = playerDice.sum()
    val newComputerDice = computerDice.map { if (it < 4) Random.nextInt(1, 7) else it }
    val computerTotal = newComputerDice.sum()

    updateComputer(newComputerDice)
    setPlayerScore(playerTotal)
    setComputerScore(computerTotal)
}

fun getDiceImage(diceValue: Int): Int {
    return when (diceValue) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1
    }
}

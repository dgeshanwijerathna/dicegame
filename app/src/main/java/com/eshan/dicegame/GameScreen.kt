//package com.eshan.dicegame
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import kotlin.random.Random
//
//@Composable
//fun GameScreen() {
//    var playerDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
//    var computerDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text("Dice Game", fontSize = 28.sp, modifier = Modifier.padding(bottom = 20.dp))
//
//        // Player's Dice
//        Text("Your Dice:", fontSize = 20.sp)
//        DiceRow(diceValues = playerDice)
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // Computer's Dice
//        Text("Computer's Dice:", fontSize = 20.sp)
//        DiceRow(diceValues = computerDice)
//        Spacer(modifier = Modifier.height(30.dp))
//
//        // Buttons
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            Button(
//                onClick = {
//                    playerDice = List(5) { Random.nextInt(1, 7) }
//                    computerDice = List(5) { Random.nextInt(1, 7) }
//                },
//                shape = RoundedCornerShape(10.dp)
//            ) {
//                Text("Throw", fontSize = 18.sp)
//            }
//
//            Button(
//                onClick = { /* Implement scoring logic here */ },
//                shape = RoundedCornerShape(10.dp)
//            ) {
//                Text("Score", fontSize = 18.sp)
//            }
//        }
//    }
//}
//
//// Composable to display a row of dice images
//@Composable
//fun DiceRow(diceValues: List<Int>) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        diceValues.forEach { value ->
//            Image(
//                painter = painterResource(id = getDiceImage(value)),
//                contentDescription = "Dice $value",
//                modifier = Modifier
//                    .size(60.dp)
//                    .padding(4.dp)
//            )
//        }
//    }
//}
//
//// Function to map dice values to image resources
//fun getDiceImage(diceValue: Int): Int {
//    return when (diceValue) {
//        1 -> R.drawable.dice_1
//        2 -> R.drawable.dice_2
//        3 -> R.drawable.dice_3
//        4 -> R.drawable.dice_4
//        5 -> R.drawable.dice_5
//        6 -> R.drawable.dice_6
//        else -> R.drawable.dice_1
//    }
//}

package com.eshan.dicegame

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        DiceRow(diceValues = playerDice, selectedDice, onDiceClick = { index ->
            selectedDice = selectedDice.toMutableList().also { it[index] = !it[index] }
        })
        Spacer(modifier = Modifier.height(20.dp))

        // Computer's Dice
        Text("Computer's Dice:", fontSize = 20.sp)
        DiceRow(diceValues = computerDice, List(5) { false })
        Spacer(modifier = Modifier.height(30.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (rollsLeft > 1) {
                        playerDice = playerDice.mapIndexed { index, value ->
                            if (selectedDice[index]) value else Random.nextInt(1, 7)
                        }
                        rollsLeft--
                    } else {
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
                },
                shape = RoundedCornerShape(10.dp),
                enabled = rollsLeft > 0
            ) {
                Text(if (rollsLeft > 1) "Re-roll ($rollsLeft left)" else "Final Roll", fontSize = 18.sp)
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
fun DiceRow(diceValues: List<Int>, selectedDice: List<Boolean>, onDiceClick: (Int) -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        diceValues.forEachIndexed { index, value ->
            Image(
                painter = painterResource(id = getDiceImage(value)),
                contentDescription = "Dice $value",
                modifier = Modifier
                    .size(60.dp)
                    .padding(4.dp)
                    .clickable { onDiceClick(index) }
                    .then(if (selectedDice[index]) Modifier.padding(2.dp) else Modifier)
            )
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
    val newComputerDice = List(5) { Random.nextInt(1, 7) }
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


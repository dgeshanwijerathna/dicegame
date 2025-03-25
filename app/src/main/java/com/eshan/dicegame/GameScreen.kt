package com.eshan.dicegame

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun GameScreen() {
    var targetScore by rememberSaveable { mutableStateOf(101) }
    var targetInput by rememberSaveable { mutableStateOf("101") }
    var gameStarted by rememberSaveable { mutableStateOf(false) }
    var playerScore by rememberSaveable { mutableStateOf(0) }
    var computerScore by rememberSaveable { mutableStateOf(0) }
    var rollsLeft by rememberSaveable { mutableStateOf(3) }
    var winner by rememberSaveable { mutableStateOf<String?>(null) }
    var playerDice by rememberSaveable { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    var computerDice by rememberSaveable { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    var selectedDice by rememberSaveable { mutableStateOf(List(5) { false }) }


    // Track total wins for both players (resets when the app restarts)
    var humanWins by rememberSaveable { mutableStateOf(0) }
    var computerWins by rememberSaveable { mutableStateOf(0) }


    if (!gameStarted) {
        var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }


        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Enter Target Score:", fontSize = 20.sp)
            TextField(
                value = targetInput,
                onValueChange = {
                    targetInput = it
                    errorMessage = null
                },
                label = { Text("Target Score") }
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                val inputNumber = targetInput.toIntOrNull()
                when {
                    inputNumber == null -> errorMessage = "Invalid input! Please enter a valid number."
                    inputNumber < 101 -> errorMessage = "Target score must be at least 101. Please enter a higher value."
                    else -> {
                        targetScore = inputNumber
                        gameStarted = true
                    }
                }
            }) {
                Text("Start Game")
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Display total wins at the top left
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Text("H:$humanWins / C:$computerWins", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("Player Score: $playerScore", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Computer Score: $computerScore", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))

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

            Text("Computer's Dice:", fontSize = 20.sp)
            DiceRow(diceValues = computerDice, selectedDice = List(5) { false }, isSelectionEnabled = false)
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (rollsLeft > 0) {
                            playerDice = playerDice.mapIndexed { index, value ->
                                if (rollsLeft == 3 || !selectedDice[index]) Random.nextInt(1, 7) else value
                            }
                            if (rollsLeft == 3) {
                                computerDice = List(5) { Random.nextInt(1, 7) }
                            }
                            rollsLeft--
                            if (rollsLeft == 0) {
                                updateScore(playerDice, computerDice, { newComputerDice -> computerDice = newComputerDice }, { score -> playerScore += score }, { score -> computerScore += score })
                                rollsLeft = 3
                                selectedDice = List(5) { false }
                                if (playerScore >= targetScore && computerScore >= targetScore) {
                                    if (playerScore == computerScore) {
                                        tieBreakerRound { newWinner -> winner = newWinner }
                                    } else {
                                        winner = if (playerScore > computerScore) "Player" else "Computer"
                                    }
                                } else if (playerScore >= targetScore) {
                                    winner = "Player"
                                } else if (computerScore >= targetScore) {
                                    winner = "Computer"
                                }
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
                        while (rollsLeft > 0) {
                            computerDice = computerDice.map { if (Random.nextBoolean()) Random.nextInt(1, 7) else it }
                            rollsLeft--
                        }
                        updateScore(playerDice, computerDice, { newComputerDice -> computerDice = newComputerDice },
                            { score -> playerScore += score }, { score -> computerScore += score })
                        rollsLeft = 3
                        selectedDice = List(5) { false }
                        if (playerScore >= targetScore || computerScore >= targetScore) {
                            winner = if (playerScore > computerScore) "Player" else "Computer"
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    enabled = rollsLeft < 3
                ) {
                    Text("Score", fontSize = 18.sp)
                }
            }
        }
    }

    if (winner != null) {
        WinnerDialog(winner!!) {
            if (winner == "Player") {
                humanWins++ // Increase human win count
            } else {
                computerWins++ // Increase computer win count
            }
            playerScore = 0
            computerScore = 0
            rollsLeft = 3
            winner = null
            gameStarted = false
        }
    }
}



//@Composable
//fun WinnerDialog(winner: String, onDismiss: () -> Unit) {
//    val context = LocalContext.current
//    val activity = remember { context as? Activity }  // Get current activity context
//
//    AlertDialog(
//        onDismissRequest = {
//            activity?.finish()  // Finish the activity when back button is pressed
//        },
//        title = {
//            Text(
//                text = if (winner == "Player") "You Win!" else "You Lose!",
//                fontSize = 24.sp,
//                color = if (winner == "Player") Color.Green else Color.Red
//            )
//        },
//        confirmButton = {
//            Button(onClick = {
//                activity?.finish()  // Finish the activity when "Play Again" is clicked
//            }) {
//                Text("Play Again")
//            }
//        }
//    )
//}

//@Composable
//fun WinnerDialog(winner: String, onDismiss: () -> Unit) {
//    AlertDialog(
//        onDismissRequest = { onDismiss() },
//        title = {
//            Text(
//                text = if (winner == "Player") "You Win!" else "You Lose!",
//                fontSize = 24.sp,
//                color = if (winner == "Player") Color.Green else Color.Red
//            )
//        },
//        confirmButton = {
//            Button(onClick = { onDismiss() }) {
//                Text("Play Again")
//            }
//        }
//    )
//}

@Composable
fun WinnerDialog(winner: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = if (winner == "Player") "You Win!" else "You Lose!",
                fontSize = 24.sp,
                color = if (winner == "Player") Color.Green else Color.Red
            )
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Play Again")
            }
        }
    )
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

fun tieBreakerRound(setWinner: (String) -> Unit) {
    var playerTieBreaker: Int
    var computerTieBreaker: Int

    do {
        playerTieBreaker = List(5) { Random.nextInt(1, 7) }.sum()
        computerTieBreaker = List(5) { Random.nextInt(1, 7) }.sum()
    } while (playerTieBreaker == computerTieBreaker)  // Keep rolling until there's a winner

    setWinner(if (playerTieBreaker > computerTieBreaker) "Player" else "Computer")
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

package com.example.wordleapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordleapp.ui.theme.WordleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WordleApp()
                }
            }
        }
    }
}

@Composable
fun WordleApp() {

    val maxAttempts = 6

    var numAttempts  by remember {
        mutableStateOf(0)
    }

    val newWord = getRandomWord()
    var word by remember {
        mutableStateOf(newWord)
    }

    var currentAttempt by remember {
        mutableIntStateOf(0)
    }

    var valueCurrentAttempt by remember {
        mutableStateOf("")
    }

    var textFieldValue by remember {
        mutableStateOf("")
    }

    var winner by remember {
        mutableStateOf(false)
    }

    var attemptsList by remember {
        mutableStateOf(listOf<String>())
    }

    var attemptsListTips by remember {
        mutableStateOf(listOf<String>())
    }

    val onValueChange = { index: Int, newValue: String ->
        valueCurrentAttempt = if (index == 0) {
            newValue
        } else {
            valueCurrentAttempt + newValue
        }
    }

    val submit: () -> Unit = {
        numAttempts++
        currentAttempt++
        attemptsList = attemptsList + valueCurrentAttempt
        val tips = getTips(word, valueCurrentAttempt)
        attemptsListTips = attemptsListTips + tips

        if (tips == word) {
            winner = true
        }

        valueCurrentAttempt = ""
    }

    val restart: () -> Unit = {
        numAttempts = 0
        currentAttempt = 0
        valueCurrentAttempt = ""
        winner = false
        attemptsList = listOf<String>()
        attemptsListTips = listOf<String>()
        word = getRandomWord()
    }


    MainScreen(
        word = word,
        numAttempts = numAttempts,
        maxAttempts = maxAttempts,
        currentAttempt = currentAttempt,
        valueCurrentAttempt = valueCurrentAttempt,
        winner = winner,
        submit = submit,
        restart = restart,
        attemptsList = attemptsList,
        attemptsListTips = attemptsListTips,
        textFieldValue = textFieldValue,
        onValueChange = onValueChange,
    )
}

@Composable
fun MainScreen(
    word: String,
    numAttempts: Int,
    maxAttempts: Int,
    currentAttempt: Int,
    valueCurrentAttempt: String,
    winner: Boolean,
    attemptsList: List<String>,
    attemptsListTips: List<String>,
    submit: () -> Unit,
    restart: () -> Unit,
    textFieldValue: String,
    onValueChange: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize()
        )
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "WORDLE",
                color = Color.White,
                fontSize = 50.sp,
                fontFamily = FontFamily.Monospace
            )

            if (winner) {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "VOCÊ VENCEU!!!",
                        color = Color.White,
                        fontSize = 40.sp,
                    )
                    Button(
                        onClick = restart
                    ) {
                        Text(
                            text = "Recomeçar",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else if (currentAttempt == maxAttempts) {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "VOCÊ PERDEU!!!",
                        color = Color.White,
                        fontSize = 40.sp,
                    )
                    Text(
                        text = "A palavra era: $word",
                        color = Color.White,
                        fontSize = 20.sp,
                    )
                    Button(
                        onClick = restart
                    ) {
                        Text(
                            text = "Recomeçar",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else {
                ScoreBoard(numAttempts)
                Attempts(
                    maxAttempts,
                    currentAttempt,
                    valueCurrentAttempt,
                    attemptsList,
                    attemptsListTips,
                    textFieldValue,
                    onValueChange
                )
            }
            UserActions(currentAttempt, numAttempts, submit)
        }
    }
}

@Composable
fun ScoreBoard(numAttempts: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Tentativas: $numAttempts",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun Attempts(
    maxAttempts: Int,
    currentAttempt: Int,
    valueCurrentAttempt: String,
    attemptsList: List<String>,
    attemptsListTips: List<String>,
    textFieldValue: String,
    onValueChange: (Int, String) -> Unit
) {
    Column {
        repeat(maxAttempts) { index ->
            Row(Modifier.padding(vertical = 8.dp, horizontal = 8.dp)) {
                repeat(5) { colIndex ->
                    val isCurrentAttempt = index == currentAttempt

                    val backgroundColor = if (currentAttempt > index) {
                        when (attemptsListTips[index][colIndex]) {
                            '*' -> Color.LightGray
                            '?' -> Color.Yellow
                            else -> Color.Green
                        }
                    } else {
                        Color.LightGray
                    }

                    OutlinedTextField(
                        value = if (index == currentAttempt) {
                            valueCurrentAttempt.getOrNull(colIndex)?.toString() ?: ""
                        } else if (index < currentAttempt) {
                            attemptsList[index][colIndex].toString()
                        } else {
                            ""
                        },
                        onValueChange = { newValue ->
                            onValueChange(colIndex, newValue)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .background(backgroundColor)
                            .border(
                                width = 1.dp,
                                color = Color.Black
                            ),
                        enabled = isCurrentAttempt,
                        textStyle = TextStyle(textAlign = TextAlign.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun UserActions(currentAttempt: Int, numAttempts: Int, submit: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(
            onClick = submit
        ) {
            Text(
                text = "Submeter",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

fun getRandomWord(): String {
    val words =
        listOf(
            "final",
            "perda",
            "girar",
            "valer",
            "local",
            "vespa",
            "pedir",
            "reino",
            "magos",
            "freio",
            "lebre",
            "jantar",
            "esqui",
            "birra",
            "exato"
        )
    return words.random()
}

fun getTips(word: String, attempt: String): String {
    // --- construir a string que contém as dicas ---
    val tip = StringBuilder()
    // --- aux para armazenar as letras ---
    val aux = StringBuilder()
    for (i in word.indices) {
        // --- para cada letra, verifica se estão na mesma posição ---
        if (word[i] == attempt[i]) {
            tip.append(word[i])
            // --- para cada letra que não está na mesma posição, verifica se está presente na palavra e se já não foi verificada ---
        } else if (word.contains(attempt[i])) {
            if (!tip.contains(attempt[i]) && !aux.contains(attempt[i])) {
                aux.append(attempt[i])
                tip.append('?')
            } else {
                tip.append('*')
            }
        } else
            tip.append('*')
    }

    return tip.toString()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WordleAppTheme {
        WordleApp()
    }
}
package com.bawp.jettip_test
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bawp.jettip_test.components.InputField
import com.bawp.jettip_test.ui.theme.JetTipTestTheme
import com.bawp.jettip_test.utils.calculateTotalPerPerson
import com.bawp.jettip_test.utils.calculateTotalTips
import com.bawp.jettip_test.widgets.RoundIconButton

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
//                    TopHeader()
                    MainContent()
                }
            }
        }
    }

    @Composable
    fun MyApp(content: @Composable () -> Unit) {
        JetTipTestTheme {
            Surface(color = MaterialTheme.colors.background) {
                content()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MyApp {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                MainContent()
            }
        }
    }

    @Composable
    fun TopHeader(moneyCount: Double = 0.0) {
        Surface(
            modifier = Modifier
                .padding(15.dp)
                .height(150.dp)
                .clip(CircleShape.copy(all = CornerSize(12.dp)))
                //                .clip(RoundedCornerShape(corner = CornerSize(12.dp)))
                .fillMaxWidth(),
            color = Color(0xFFE9D7F7)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    //                .clip(RoundedCornerShape(corner = CornerSize(12.dp)))
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val total = "%.2f".format(moneyCount)
                Text(
                    text = "Total Per Person",
                    style = TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        fontFamily = FontFamily.SansSerif,
                    )
                )
                Text(
                    text = "$$total",
                    style = MaterialTheme.typography.h4
                )

            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun MainContent() {
        BillForm() { bill ->
            Log.d("BillAmount", "MainContent: >> $bill")

        }
    }

    @Composable
    fun BillForm(
        modifier: Modifier = Modifier,
        onValChange: (String) -> Unit = {}
    ) {
        val totalBillState = remember { mutableStateOf("") }
        val sliderPositionState = remember { mutableStateOf(0f) }
        val sliderPercent = remember(sliderPositionState.value) {
            mutableStateOf((sliderPositionState.value * 100).toInt())
        }
        val validState = remember(totalBillState.value) {
            totalBillState.value.trim().isNotEmpty()
        }
        val splitByState = remember { mutableStateOf(1) }
        val tipAmountState = remember { mutableStateOf(0.0) }
        val totalPerPersonState = remember { mutableStateOf(0.0) }
        val rangeItem = IntRange(start = 1, endInclusive = 100)
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            TopHeader(totalPerPersonState.value)

            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                border = BorderStroke(0.8.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    InputField(
                        valueState = totalBillState,
                        labelId = "Enter Bill",
                        enabled = true,
                        onValueChangeListener = {
                            if (it.isNotEmpty()) {
                                totalBillState.value = it.trim()
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBillState.value.toDouble(),
                                    splitByState.value,
                                    sliderPercent.value
                                )
                            } else {
                                totalBillState.value = ""
                                totalPerPersonState.value = 0.0
                            }
                        },
                        isSingleLine = true,
                        onAction = KeyboardActions{
                            if (!validState) return@KeyboardActions
                            onValChange(totalBillState.value.trim())
                            keyboardController?.hide()
                        },
                    )
                    if(validState) {
                        CreateAddRemoveButton(splitByState.value, {
                            if (it != null && it > 1) {
                                splitByState.value--
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBillState.value.toDouble(), splitByState.value, sliderPercent.value
                                )
                            }
                        }) {
                            if (it != null && it < rangeItem.last) {
                                splitByState.value++
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBillState.value.toDouble(), splitByState.value, sliderPercent.value
                                )
                            }
                        }
                        CreateTip(tipAmountState.value)
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${sliderPercent.value}%",
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(24.dp),
                            )

                            Slider(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 16.dp),
                                value = sliderPositionState.value,
                                onValueChange = { newValue ->
                                    sliderPositionState.value = newValue
                                    tipAmountState.value =
                                        calculateTotalTips(totalBillState.value, sliderPercent.value)
                                    totalPerPersonState.value = calculateTotalPerPerson(
                                        totalBillState.value.toDouble(), splitByState.value, sliderPercent.value
                                    )

                                },
                                steps = 4,
                            )
                        }
                    } else {
                        Box() {}
                    }
                    /*TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = ,
                        fontFamily = FontFamily.SansSerif
                    )*/
                }
            }
        }
    }

    @Composable
    fun CreateAddRemoveButton(split: Int, remove: ((Int?) -> Unit)? = null, add: ((Int?) -> Unit)? = null) {
        Row(
            modifier = Modifier
                .padding(3.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Split",
                modifier = Modifier
                    .padding(8.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
            Spacer(
                modifier = Modifier
                    .width(120.dp)
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                RoundIconButton(
                    imageVector = Icons.Default.Remove,
                    onClick = {
                        if (remove != null) remove(split)
                    },)

                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .align(Alignment.CenterVertically),
                    text = "$split",
                )
                RoundIconButton(
                    imageVector = Icons.Default.Add,
                    onClick = {
                        if (add != null) add(split)
                    },)
            }
        }
    }

    @Composable
    fun CreateTip(tipAmount: Double) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .padding(3.dp)
                    .align(alignment = CenterVertically),
                text = "Tips",
            )
            Spacer(modifier = Modifier.width(200.dp))
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .align(alignment = CenterVertically),
                text = "$$tipAmount",
            )
        }
    }
}
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


    /*
    @Composable
    fun MyApp( content: @Composable () -> Unit){
        */
    /*
          content: @Composable ... it's called a container function
          which makes MyApp more flexible to deal with
         *//*

        JetTipTestTheme {
            // A surface container using the 'background' color from the theme
            Surface(color = MaterialTheme.colors.background) {
                content()
            }
        }


    }
    @ExperimentalComposeUiApi
    @Composable
    fun TipCalculator() {
        Surface(modifier = Modifier
            .padding(12.dp)) {

            Column() {

                MainContent()
            }
        }

    }


    @Preview
    @Composable
    fun TopHeader(totalPerPers: Double = 0.0) {
        Surface(modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(12.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
            color = Color(0xFFe9d7f7)
               ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center) {
                Text(text = "Total Per Person",
                    style = MaterialTheme.typography.subtitle1)
               val total =  "%.2f".format(totalPerPers)
                Text(text = "\$$total",
                    style = MaterialTheme.typography.h4)


            }

        }

    }

    @ExperimentalComposeUiApi
    @Preview
    @Composable
    fun MainContent() {
        BillForm{ billAmt ->
            Log.d("Bill", "MainContent: $billAmt")
        }
    }

    fun calculateTotalTip(totalBill: Double, tipPercent: Int): Double {

        return if (totalBill > 1 && totalBill.toString().isNotEmpty()) (totalBill * tipPercent) / 100 else 0.0
    }
    fun calculateTotalPerPerson(totalBill: Double, splitBy: Int, tipPercent: Int): Double {
        val bill = calculateTotalTip(totalBill, tipPercent = tipPercent) + totalBill
        return (bill/splitBy)
    }
    @ExperimentalComposeUiApi
    @Composable
    fun BillForm(modifier: Modifier = Modifier,
                 onValChange: (String) -> Unit
                ) {

        val splitBy = remember {
            mutableStateOf(1)
        }

        var sliderPosition by remember {
            mutableStateOf(0f)
        }
        val totalTipAmt = remember {
            mutableStateOf(0.0)
        }
        val totalPerPerson = remember {
            mutableStateOf(0.0)
        }

        val tipPercentage = (sliderPosition * 100).roundToInt()
        val totalBill = rememberSaveable{ mutableStateOf("") } //or just remember {}
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(totalBill.value) {
            totalBill.value.trim().isNotEmpty()
        }

        TopHeader(totalPerPers = totalPerPerson.value)

        Surface(modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .height(260.dp),
            shape = CircleShape.copy(all = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)) {

            Column(modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start) {

                InputField(
                    valueState = totalBill, labelId = "Enter Bill" ,
                    enabled = true, onAction = KeyboardActions {
                        //The submit button is disabled unless the inputs are valid. wrap this in if statement to accomplish the same.
                        if (!valid) return@KeyboardActions
                        onValChange(totalBill.value.trim())
                        //totalBill.value = ""
                        keyboardController?.hide() //(to use this we need to use @ExperimentalComposeUiApi
                    }, )
                Row(modifier = Modifier.padding(3.dp),
                   horizontalArrangement = Arrangement.Start) {
                    Text(text = "Split", modifier = Modifier
                        .align(alignment = Alignment.CenterVertically))

                    Spacer(modifier = Modifier.width(120.dp))

                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End) {
                        CustomButton(signLabel = "-"){
                            splitBy.value = if (splitBy.value > 1) splitBy.value - 1 else  1
                            totalPerPerson.value = calculateTotalPerPerson(
                                totalBill = totalBill.value.toDouble(),
                                splitBy = splitBy.value,
                                tipPercent = tipPercentage)
                           // Log.d("TAG", "BillForm-Minus: ${splitCounter.value}")
                        }
                        Text(text = "${splitBy.value}",
                            Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp))
                        CustomButton(count = splitBy.value, signLabel = "+"){ newVal ->
                            splitBy.value = newVal
                            totalPerPerson.value = calculateTotalPerPerson(
                                totalBill = totalBill.value.toDouble(),
                                splitBy = splitBy.value,
                                tipPercent = tipPercentage)
                            Log.d("TAG", "BillForm: ${splitBy.value}")
                        }

                    }
                }
                //Tip Row
                Row(modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.End) {
                    Text(text = "Tip", modifier = Modifier
                        .align(alignment = Alignment.CenterVertically))

                    Spacer(modifier = Modifier.width(200.dp))

                    Text(text = "$${totalTipAmt.value}", modifier = Modifier
                        .align(alignment = Alignment.CenterVertically))

                }

                //Slider
                Column(verticalArrangement = Arrangement.Center,
                      horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(text = "$tipPercentage %")
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(value = sliderPosition, onValueChange = { newVal ->
                        sliderPosition = newVal
                         totalTipAmt.value = calculateTotalTip(totalBill = totalBill.value.toDouble(),
                            tipPercent = tipPercentage)

                        totalPerPerson.value = calculateTotalPerPerson(
                            totalBill = totalBill.value.toDouble(),
                            splitBy = splitBy.value,
                            tipPercent = tipPercentage)
                        Log.d("Slider", "Total Bill-->: ${"%.2f".format(totalTipAmt.value)}")
                    }, modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 5,
                          onValueChangeFinished = {
                              Log.d("Finished", "BillForm: $tipPercentage")
                              //This is were the calculations should happen!
                          })

                }

            }

        }


    }

    @Composable
    fun CustomButton(
        modifier: Modifier = Modifier,
        count: Int = 1,
        signLabel: String = "+",
        onClickButton: (Int) -> Unit = {},
                    ) {

        Button(onClick = {
            if (signLabel == "-"){

                onClickButton(count - 1)
            }else {
                onClickButton(count + 1)
            }

           // Log.d("TAG", "CustomButton: ${count}")
        },
            modifier = modifier
                .width(40.dp)
                .height(40.dp),
              colors  = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFe9d7f7))) {
            Text(text = signLabel,
                style = TextStyle(fontWeight = FontWeight.ExtraBold))

        }

    }



    @Composable
    fun OutlinedTextField(modifier: Modifier = Modifier, label: String ="lala", onValChange: (String) -> Unit = {}) {
        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            value = text,
            onValueChange = { onValChange(it); text = it},
            label = { Text(label) },
            leadingIcon = { Icon(imageVector = Icons.Default.Money , contentDescription = "lala")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = {
                this.defaultKeyboardAction(imeAction = ImeAction.Done)}),


            )
    }
    */

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
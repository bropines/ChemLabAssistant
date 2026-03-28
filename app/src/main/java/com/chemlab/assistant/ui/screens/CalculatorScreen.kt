package com.chemlab.assistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun CalculatorScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Массовая доля", "Молярность", "Объемная доля")

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
            text = "Калькулятор растворов",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Box(modifier = Modifier.padding(16.dp)) {
            when (selectedTabIndex) {
                0 -> MassFractionCalculator()
                1 -> MolarityCalculator()
                2 -> VolumeFractionCalculator()
            }
        }
    }
}

@Composable
fun MassFractionCalculator() {
    var massSolution by remember { mutableStateOf("") }
    var massFraction by remember { mutableStateOf("") }
    var useHydrate by remember { mutableStateOf(false) }
    var molarMassAnhydrous by remember { mutableStateOf("") }
    var waterMolecules by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = massSolution,
            onValueChange = { massSolution = it },
            label = { Text("Масса раствора (г)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = massFraction,
            onValueChange = { massFraction = it },
            label = { Text("Массовая доля (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = useHydrate, onCheckedChange = { useHydrate = it })
            Text("Учитывать кристаллогидрат")
        }

        if (useHydrate) {
            OutlinedTextField(
                value = molarMassAnhydrous,
                onValueChange = { molarMassAnhydrous = it },
                label = { Text("Молярная масса б/в соли") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = waterMolecules,
                onValueChange = { waterMolecules = it },
                label = { Text("Молекул воды (n)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        val result = calculateMass(massSolution, massFraction, useHydrate, molarMassAnhydrous, waterMolecules)
        if (result != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результат", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${if (useHydrate) "Масса гидрата" else "Масса в-ва"}: ${result.mSolute} г")
                    Text("Масса воды: ${result.mSolvent} г")
                }
            }
        }
    }
}

data class MassResult(val mSolute: String, val mSolvent: String)

fun calculateMass(mSolStr: String, wStr: String, useHydrate: Boolean, mmAnhStr: String, nH2OStr: String): MassResult? {
    val mSol = mSolStr.replace(",", ".").toFloatOrNull() ?: return null
    val w = wStr.replace(",", ".").toFloatOrNull() ?: return null
    if (w <= 0 || w > 100) return null

    val mSolute = (mSol * w) / 100

    if (useHydrate) {
        val mmAnh = mmAnhStr.replace(",", ".").toFloatOrNull() ?: return null
        val nH2O = nH2OStr.replace(",", ".").toFloatOrNull() ?: return null
        if (mmAnh <= 0 || nH2O <= 0) return null

        val mmWater = 18.015 * nH2O
        val mmHydrate = mmAnh + mmWater
        val mHydrate = (mSolute * mmHydrate) / mmAnh
        val mSolvent = mSol - mHydrate

        return MassResult(String.format(Locale.US, "%.5f", mHydrate), String.format(Locale.US, "%.5f", mSolvent))
    }

    val mSolvent = mSol - mSolute
    return MassResult(String.format(Locale.US, "%.5f", mSolute), String.format(Locale.US, "%.5f", mSolvent))
}

@Composable
fun MolarityCalculator() {
    var volume by remember { mutableStateOf("") }
    var molarity by remember { mutableStateOf("") }
    var molarMass by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = volume, onValueChange = { volume = it },
            label = { Text("Объем раствора (мл)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = molarity, onValueChange = { molarity = it },
            label = { Text("Молярность (М, моль/л)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = molarMass, onValueChange = { molarMass = it },
            label = { Text("Молярная масса (г/моль)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        val v = volume.replace(",", ".").toFloatOrNull()
        val c = molarity.replace(",", ".").toFloatOrNull()
        val mm = molarMass.replace(",", ".").toFloatOrNull()

        if (v != null && c != null && mm != null && v > 0 && c > 0 && mm > 0) {
            val mSolute = c * (v / 1000) * mm
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Масса в-ва: ${String.format(Locale.US, "%.5f", mSolute)} г", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun VolumeFractionCalculator() {
    var vSolute by remember { mutableStateOf("") }
    var vSolution by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = vSolute, onValueChange = { vSolute = it },
            label = { Text("Объем растворенного в-ва (мл)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = vSolution, onValueChange = { vSolution = it },
            label = { Text("Объем раствора (мл)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        val vs = vSolute.replace(",", ".").toFloatOrNull()
        val vsol = vSolution.replace(",", ".").toFloatOrNull()

        if (vs != null && vsol != null && vsol > 0 && vs <= vsol) {
            val phi = (vs / vsol) * 100
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Объемная доля (φ): ${String.format(Locale.US, "%.5f", phi)} %", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

package com.abdul.salarycalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.abdul.salarycalculator.presentation.navigation.AppNavigation
import com.abdul.salarycalculator.ui.theme.SalaryCalculatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SalaryCalculatorTheme {
                AppNavigation()
            }
        }
    }
}

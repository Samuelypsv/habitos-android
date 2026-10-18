package com.samuelypsv.habitos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.samuelypsv.habitos.ui.HabitosScreen
import com.samuelypsv.habitos.ui.HabitosViewModel
import com.samuelypsv.habitos.ui.theme.HabitosTheme

class MainActivity : ComponentActivity() {

    private val viewModel: HabitosViewModel by viewModels {
        HabitosViewModel.factory((application as HabitosApp).repositorio)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitosTheme {
                HabitosScreen(viewModel)
            }
        }
    }
}

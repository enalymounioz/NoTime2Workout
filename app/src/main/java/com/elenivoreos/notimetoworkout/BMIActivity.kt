package com.elenivoreos.notimetoworkout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elenivoreos.notimetoworkout.databinding.ActivityBmiBinding

class BMIActivity : AppCompatActivity(){
    private var binding: ActivityBmiBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarBmiActivity)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = getString(R.string.calculate_bmi)
        }
        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}
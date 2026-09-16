package com.endviyou.bugs.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.endviyou.bugs.R

class SettingsFragment : Fragment() {

    // Элементы
    private lateinit var seekBarSpeed: SeekBar
    private lateinit var seekBarMaxBugs: SeekBar
    private lateinit var seekBarBonusInterval: SeekBar
    private lateinit var seekBarRoundDuration: SeekBar

    private lateinit var tvSpeedValue: TextView
    private lateinit var tvMaxBugsValue: TextView
    private lateinit var tvBonusIntervalValue: TextView
    private lateinit var tvRoundDurationValue: TextView

    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        loadSavedSettings()
        setupSeekBars()
        setupSaveButton()
    }

    private fun initViews(view: View) {
        seekBarSpeed = view.findViewById(R.id.seekBarSpeed)
        seekBarMaxBugs = view.findViewById(R.id.seekBarMaxBugs)
        seekBarBonusInterval = view.findViewById(R.id.seekBarBonusInterval)
        seekBarRoundDuration = view.findViewById(R.id.seekBarRoundDuration)

        tvSpeedValue = view.findViewById(R.id.tvSpeedValue)
        tvMaxBugsValue = view.findViewById(R.id.tvMaxBugsValue)
        tvBonusIntervalValue = view.findViewById(R.id.tvBonusIntervalValue)
        tvRoundDurationValue = view.findViewById(R.id.tvRoundDurationValue)

        btnSave = view.findViewById(R.id.btnSaveSettings)
    }

    private fun setupSeekBars() {
        // 1. Скорость игры (1-10)
        seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val speed = progress + 1  // 1-10
                tvSpeedValue.text = "$speed"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 2. Максимум тараканов (1-20)
        seekBarMaxBugs.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val maxBugs = progress + 1  // 1-20
                tvMaxBugsValue.text = "$maxBugs"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 3. Интервал бонусов (1-30 сек)
        seekBarBonusInterval.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val interval = progress + 1  // 1-30
                tvBonusIntervalValue.text = "$interval сек"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 4. Длительность раунда (30-180 сек)
        seekBarRoundDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val duration = progress + 30  // 30-180
                tvRoundDurationValue.text = "$duration сек"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    /**
     * Загрузка сохранённых настроек из SharedPreferences
     */
    private fun loadSavedSettings() {
        val prefs = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)

        val speed = prefs.getInt("speed", 5)
        val maxBugs = prefs.getInt("max_bugs", 10)
        val bonusInterval = prefs.getInt("bonus_interval", 10)
        val roundDuration = prefs.getInt("round_duration", 60)

        // Устанавливаем значения
        seekBarSpeed.progress = speed - 1
        seekBarMaxBugs.progress = maxBugs - 1
        seekBarBonusInterval.progress = bonusInterval - 1
        seekBarRoundDuration.progress = roundDuration - 30

        // Обновляем тексты
        tvSpeedValue.text = "$speed"
        tvMaxBugsValue.text = "$maxBugs"
        tvBonusIntervalValue.text = "$bonusInterval сек"
        tvRoundDurationValue.text = "$roundDuration сек"
    }

    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            val prefs = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)
            val editor = prefs.edit()

            editor.putInt("speed", seekBarSpeed.progress + 1)
            editor.putInt("max_bugs", seekBarMaxBugs.progress + 1)
            editor.putInt("bonus_interval", seekBarBonusInterval.progress + 1)
            editor.putInt("round_duration", seekBarRoundDuration.progress + 30)

            editor.apply()

            Toast.makeText(
                requireContext(),
                "Настройки сохранены!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
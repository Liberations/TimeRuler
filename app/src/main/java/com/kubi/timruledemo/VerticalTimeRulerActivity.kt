package com.kubi.timruledemo

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.kubi.timeruler.BaseScaleBarVertical
import com.kubi.timeruler.TimeRulerBarVertical
import com.kubi.timruledemo.databinding.ActivityVerticalTimeRulerBinding
import java.text.SimpleDateFormat
import java.util.*

class VerticalTimeRulerActivity : AppCompatActivity() {
    lateinit var binding: ActivityVerticalTimeRulerBinding
    val cursorDateFormat = SimpleDateFormat("MM月dd日 HH:mm:ss")
    var nowTime = 1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerticalTimeRulerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTimeBar()
    }

    private fun initTimeBar() {
        val calendar = Calendar.getInstance()

        // 00:00:00 000
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        var startTime = calendar.timeInMillis

        // 23:59:59 999
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        calendar[Calendar.SECOND] = 59
        calendar[Calendar.MILLISECOND] = 999
        var endTime = calendar.timeInMillis

        binding.timeBarVertical.setRange(startTime, endTime)
        binding.timeBarVertical.setMode(TimeRulerBarVertical.MODE_UINT_30_MIN)
        binding.timeBarVertical.setCursorValue(System.currentTimeMillis())

        binding.timeBarVertical.setOnCursorListener(object : BaseScaleBarVertical.OnCursorListener {
            override fun onStartTrackingTouch(cursorValue: Long) {

            }

            override fun onProgressChanged(cursorValue: Long, fromeUser: Boolean) {
                //监听时间戳变化
                binding.tvDataVertical.text = cursorDateFormat.format(Date(cursorValue))
            }

            override fun onStopTrackingTouch(cursorValue: Long) {

            }
        })

        binding.seekBarVertical.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.timeBarVertical.setScale(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        binding.seekAreaOffsetVertical.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.timeBarVertical.setVideoAreaOffset(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        binding.btnDirVertical.setOnClickListener {
            binding.btnDirVertical.isSelected = !binding.btnDirVertical.isSelected
            binding.timeBarVertical.setTickDirection(binding.btnDirVertical.isSelected)
        }

        binding.btnShowCursorVertical.setOnClickListener {
            binding.btnShowCursorVertical.isSelected = !binding.btnShowCursorVertical.isSelected
            binding.timeBarVertical.setShowCursor(binding.btnShowCursorVertical.isSelected);
        }

        binding.btnPlayVertical.setOnClickListener {
            nowTime++
            binding.timeBarVertical.setCursorValue(System.currentTimeMillis() + 1000 * nowTime * 60)
        }
        setData()

    }

    private fun setData() {
        var videos = mutableListOf<VideoBean>()
        var testTime = System.currentTimeMillis()
        for (i in 1..5) {
            val video = VideoBean(testTime, testTime + i * 10 * 60 * 1000, i % 2 == 0)
            videos.add(video)
            testTime += i * 15 * 60 * 1000
        }
        val timeBean = TimeBean(videos)
        binding.timeBarVertical.setColorScale(timeBean)
    }
}

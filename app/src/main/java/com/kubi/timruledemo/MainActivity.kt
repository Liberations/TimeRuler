package com.kubi.timruledemo

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.kubi.timeruler.BaseScaleBar
import com.kubi.timeruler.TimeRulerBar
import com.kubi.timruledemo.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val cursorDateFormat = SimpleDateFormat("MM月dd日 HH:mm:ss")
    var nowTime = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        binding.timeBar.setRange(startTime, endTime)
        binding.timeBar.setMode(TimeRulerBar.MODE_UINT_30_MIN)
        binding.timeBar.setCursorValue(System.currentTimeMillis())

        binding.timeBar.setOnCursorListener(object : BaseScaleBar.OnCursorListener {
            override fun onStartTrackingTouch(cursorValue: Long) {

            }

            override fun onProgressChanged(cursorValue: Long, fromeUser: Boolean) {
                //监听时间戳变化
                binding.tvData.text = cursorDateFormat.format(Date(cursorValue))
            }

            override fun onStopTrackingTouch(cursorValue: Long) {

            }
        })

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.timeBar.setScale(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        binding.seekAreaOffset.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.timeBar.setVideoAreaOffset(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        binding.btnDir.setOnClickListener {
            binding.btnDir.isSelected = !binding.btnDir.isSelected
            binding.timeBar.setTickDirection(binding.btnDir.isSelected)
        }

        binding.btnShowCursor.setOnClickListener {
            binding.btnShowCursor.isSelected = !binding.btnShowCursor.isSelected
            binding.timeBar.setShowCursor(binding.btnShowCursor.isSelected);
        }

        binding.btnPlay.setOnClickListener {
            nowTime++
            binding.timeBar.setCursorValue(System.currentTimeMillis() + 1000 * nowTime * 60)
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
        binding.timeBar.setColorScale(timeBean)
    }
}
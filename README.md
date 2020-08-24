# TimeRuler

[![](https://jitpack.io/v/Liberations/TimeRuler.svg)](https://jitpack.io/#Liberations/TimeRuler)

可以缩放平移的时间刻度尺，方便自定义UI需求。仿萤石云历史录像时间轴

## 效果:
<a href="https://gitee.com/FIUI/time-rule-view-demo/raw/master/pic/pic1.jpg" target="_blank"><p align="center"><img src="https://gitee.com/FIUI/time-rule-view-demo/raw/master/pic/pic1.jpg" alt="timeBar"></p></a>

Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    implementation 'com.github.Liberations:TimeRuler:{latest version}'
}
```

## BaseScaleBar支持的属性设置（Attributes）:
|name|format|description|
|:---:|:---:|:---:|
|keyTickHeight|dimension,reference|关键刻度高度|
|tickValueOffset|dimension,reference|刻度文字偏移|
|cursorPosition|float|游标相对view width的位置  0～ 1|
|baselinePosition|float|刻度尺横线相对view height的位置 0~1|
|tickColor|color,reference|刻度线颜色|
|showTickLine|boolean|是否显示刻度横线|
|showCursorLine|boolean|是否显示游标线|
|showTickValue|boolean|是否显示刻度值|
|tickDirectionUp|boolean|刻度线开口方向|
|cursorLineColor|color,reference|游标线颜色|
|maxScaleValueSize|color,reference|刻度值最大规格(字体大小)|


 ## TimeRulerBar支持的额外属性（Attributes）:
|name|format|description|
|:---:|:---:|:---:|
|tickValueColor|color，reference|刻度值颜色|
|colorScaleBackground|color,reference|颜色区域背景色|
|cursorBackgroundColor|color，reference|游标背景色|
|drawCursorContent|boolean|是否绘制游标内容|
|tickValueSize|dimension|reference|刻度值文字大小|
|videoAreaOffset|dimension|reference|绘制颜色区域相对于顶部的偏移量|
|videoAreaHeight|dimension,reference|绘制颜色区域的高度|
|tickValueColor|color,reference|刻度值颜色|

## 基本使用

```
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
        //设置刻度尺时间范围
        timeBar.setRange(startTime, endTime)
        //设置初始化缩放模式
        timeBar.setMode(TimeRulerBar.MODE_UINT_30_MIN)
        //设置当前刻度值
        timeBar.setCursorValue(System.currentTimeMillis())
```

## 滑动事件监听

```
       timeBar.setOnCursorListener(object : BaseScaleBar.OnCursorListener {
            override fun onStartTrackingTouch(cursorValue: Long) {
              //开滑动

            }

            override fun onProgressChanged(cursorValue: Long,fromeUser:Boolean) {
               //刻度发生变化 fromeUser是否触摸事件触发
            }

            override fun onStopTrackingTouch(cursorValue: Long) {
               //结束滑动

            }
        })
```
## 更多方法参照Demo
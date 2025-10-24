# TimeRuler 纵向滚动功能

## 概述

TimeRuler库现在支持纵向滚动功能！除了原有的横向滚动`TimeRulerBar`，我们新增了纵向滚动的`TimeRulerBarVertical`组件。

## 新增组件

### 1. BaseScaleBarVertical
- 纵向滚动的基础类
- 支持垂直方向的时间轴滚动
- 提供与横向版本相同的功能，但适配了纵向布局

### 2. TimeRulerBarVertical  
- 纵向滚动的时间尺组件
- 继承自`BaseScaleBarVertical`
- 提供完整的时间轴功能，包括缩放、拖拽、惯性滚动等

## 使用方法

### 在XML布局中使用

```xml
<com.kubi.timeruler.TimeRulerBarVertical
    android:id="@+id/timeBarVertical"
    android:layout_width="82dp"
    android:layout_height="match_parent"
    android:background="#141414"
    app:baselinePositionVertical="0.7"
    app:colorScaleBackgroundVertical="#141414"
    app:cursorBackgroundColorVertical="@android:color/holo_red_dark"
    app:drawCursorContentVertical="false"
    app:showCursorLineVertical="true"
    app:tickColorVertical="#ffffff"
    app:tickDirectionLeftVertical="false"
    app:tickValueColorVertical="#ffffff"
    app:tickValueOffsetVertical="-30dp"
    app:tickValueSizeVertical="10dp"
    app:videoAreaWidthVertical="20dp"
    app:videoAreaOffsetVertical="30dp" />
```

### 在代码中配置

```kotlin
// 设置时间范围
val calendar = Calendar.getInstance()
calendar[Calendar.HOUR_OF_DAY] = 0
calendar[Calendar.MINUTE] = 0
calendar[Calendar.SECOND] = 0
calendar[Calendar.MILLISECOND] = 0
val startTime = calendar.timeInMillis

calendar[Calendar.HOUR_OF_DAY] = 23
calendar[Calendar.MINUTE] = 59
calendar[Calendar.SECOND] = 59
calendar[Calendar.MILLISECOND] = 999
val endTime = calendar.timeInMillis

timeBarVertical.setRange(startTime, endTime)
timeBarVertical.setMode(TimeRulerBarVertical.MODE_UINT_30_MIN)
timeBarVertical.setCursorValue(System.currentTimeMillis())

// 设置监听器
timeBarVertical.setOnCursorListener(object : BaseScaleBarVertical.OnCursorListener {
    override fun onStartTrackingTouch(cursorValue: Long) {
        // 开始拖拽
    }

    override fun onProgressChanged(cursorValue: Long, fromUser: Boolean) {
        // 时间值变化
        val timeText = SimpleDateFormat("HH:mm:ss").format(Date(cursorValue))
    }

    override fun onStopTrackingTouch(cursorValue: Long) {
        // 停止拖拽
    }
})
```

## 主要特性

### 1. 纵向滚动支持
- 支持垂直方向的拖拽滚动
- 支持惯性滚动
- 支持缩放功能

### 2. 刻度线方向控制
- `tickDirectionLeft`: 控制刻度线向左还是向右延伸
- 默认值为`true`（向左）

### 3. 游标位置控制
- `cursorPosition`: 游标在视图中的相对位置（0-1）
- 默认值为0.5（中间位置）

### 4. 基线位置控制
- `baselinePosition`: 刻度尺基线在视图中的相对位置（0-1）
- 默认值为0.67

## 属性说明

### BaseScaleBarVertical 属性
- `keyTickWidthVertical`: 关键刻度宽度
- `tickValueOffsetVertical`: 文字偏移量
- `normalTickRatioVertical`: 普通刻度与关键刻度的比率
- `cursorPositionVertical`: 游标相对位置
- `baselinePositionVertical`: 基线相对位置
- `tickColorVertical`: 刻度线颜色
- `showTickLineVertical`: 是否显示刻度线
- `showCursorLineVertical`: 是否显示游标线
- `showTickValueVertical`: 是否显示刻度值
- `tickDirectionLeftVertical`: 刻度线方向
- `cursorLineColorVertical`: 游标线颜色
- `maxScaleValueSizeVertical`: 刻度值最大字体大小

### TimeRulerBarVertical 属性
- `tickValueColorVertical`: 刻度值颜色
- `videoAreaWidthVertical`: 视频区域宽度
- `videoAreaOffsetVertical`: 视频区域偏移量
- `tickValueSizeVertical`: 刻度值字体大小
- `drawCursorContentVertical`: 是否绘制游标内容
- `cursorBackgroundColorVertical`: 游标背景颜色
- `cursorValueSizeVertical`: 游标值字体大小
- `colorScaleBackgroundVertical`: 颜色刻度尺背景

## 模式支持

纵向滚动组件支持与横向版本相同的时间模式：

- `MODE_UINT_1_MIN`: 1分钟单位
- `MODE_UINT_5_MIN`: 5分钟单位  
- `MODE_UINT_10_MIN`: 10分钟单位
- `MODE_UINT_30_MIN`: 30分钟单位
- `MODE_UINT_1_HOUR`: 1小时单位

## 示例项目

查看 `VerticalTimeRulerActivity` 了解完整的使用示例，包括：
- 基本配置
- 事件监听
- 缩放控制
- 游标显示控制
- 刻度线方向控制

## 注意事项

1. 纵向滚动组件使用画布旋转来实现垂直布局，性能与横向版本相当
2. 触摸事件已适配纵向滚动，使用`distanceY`而不是`distanceX`
3. 惯性滚动同样适配了纵向方向
4. 所有原有的功能都得到保留，只是方向从横向变为纵向
5. 游标绘制和颜色区域绘制已完全适配纵向布局
6. 所有public方法都添加了详细的JavaDoc注释

## 兼容性

- 与原有横向滚动组件完全兼容
- 可以同时使用横向和纵向组件
- 共享相同的数据模型和接口

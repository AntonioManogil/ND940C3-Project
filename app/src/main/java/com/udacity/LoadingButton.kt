package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var clickedColor = 0
    private var loadingColor = 0
    private var completedColor = 0
    private var textColor = 0
    private var buttonColor = 0
    private var arcColor = 0
    private var clickedText = ""
    private var loadingText = ""
    private var completedText = ""

    private var widthSize = 0
    private var heightSize = 0

    private var textWidth = 0
    private var textHeight = 0

    private var textShown = ""

    @Volatile
    private var progress: Int = 0

    private var valueAnimator = ValueAnimator()
    private var bounds = Rect()
    private val sideSize = 70f
    private var selectedOption = false

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Clicked) { _, _, new ->
        when(new){
            ButtonState.Clicked -> {
                textShown = clickedText
                buttonColor = clickedColor
                valueAnimator.cancel()
            }
            ButtonState.Loading -> {
                textShown = loadingText
                valueAnimator.start()
                this.isEnabled = false
                requestLayout()
            }
            ButtonState.Completed -> {
                textShown = completedText
                buttonColor = completedColor
            }
        }

        paint.getTextBounds(textShown, 0, textShown.length, bounds)
        textHeight =  bounds.height()
        textWidth =  bounds.width()

        // Calculating rectangle where we'll draw the arc
        rect = RectF(
            ((widthSize/2) + textWidth).toFloat() - (sideSize * 2f),
            (heightSize - textHeight  + 6f -sideSize),
            ((widthSize/2) + textWidth).toFloat() - sideSize,
            (heightSize - textHeight  + 6f)
        )
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    private lateinit var rect: RectF


    init {
        isClickable = true
        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.LoadingButton)
        clickedColor=typedArray.getColor(R.styleable.LoadingButton_clickedColor,0)
        loadingColor = typedArray.getColor(R.styleable.LoadingButton_loadingColor,0)
        completedColor = typedArray.getColor(R.styleable.LoadingButton_completedColor,0)
        textColor = typedArray.getColor(R.styleable.LoadingButton_textColor, 0)
        arcColor = typedArray.getColor(R.styleable.LoadingButton_arcColor, 0)

        clickedText = typedArray.getString(R.styleable.LoadingButton_clickedText).toString()
        loadingText = typedArray.getString(R.styleable.LoadingButton_loadingText).toString()
        completedText = typedArray.getString(R.styleable.LoadingButton_completedText).toString()
        buttonState = ButtonState.Clicked
        typedArray.recycle()

        valueAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 5000
            interpolator = LinearInterpolator()
            addUpdateListener {
                progress = this.animatedValue as Int
                invalidate()
                requestLayout()
            }
        }
        valueAnimator.repeatCount = ValueAnimator.INFINITE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = buttonColor

        if(buttonState != ButtonState.Loading) {
            // Draw a rectangle filled with clickedColor
            canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
        }else{
            // Draw a rectangle filled with loadingColor taking into account progress
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            if(progress == 100) paint.color = clickedColor else paint.color = loadingColor
            canvas.drawRect(
                0f, 0f,
                (width * (progress.toDouble() / 100)).toFloat(), height.toFloat(), paint
            )

            //Draw an arc
            paint.color = arcColor
            canvas.drawArc(rect, 0f, (360 * (progress.toDouble() / 100)).toFloat(), true, paint)
        }
        paint.color = textColor
        canvas.drawText(textShown, (widthSize/2).toFloat(), ((heightSize + paint.textSize/2)/2), paint)
    }

    override fun performClick(): Boolean{
        super.performClick()
        when(buttonState) {
            ButtonState.Clicked -> {
                if(selectedOption) buttonState = ButtonState.Loading
            }
            ButtonState.Loading -> {
                buttonState = ButtonState.Clicked
            }
            else -> {}
        }
        invalidate()
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun downLoadCompleted(){
        buttonState = ButtonState.Clicked
        this.isEnabled = true
    }

    fun selectedOption(){
        selectedOption = true
    }
}
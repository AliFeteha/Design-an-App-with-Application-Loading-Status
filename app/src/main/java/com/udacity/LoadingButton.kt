package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var heightSize = 0
    private var widthSize = 0
    private var textWidth = 0f
    private var textColor = context.getColor(R.color.white)
    private var textSize: Float = resources.getDimension(R.dimen.default_text_size)
    private var offSet = textSize / 2
    private var progressWidth = 0f
    private var circle = 0f
    private var title: String
    private var buttonStartColor = ContextCompat.getColor(context, R.color.button_color_start)
    private var buttonLoadingColor = ContextCompat.getColor(context, R.color.button_color_end)
    private var circleColor = ContextCompat.getColor(context, R.color.white)
    private var valueAnimator = ValueAnimator()
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Clicked -> {
                title = context.getString(R.string.button_loading)
                invalidate()
            }
            ButtonState.Loading -> {
                title = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                valueAnimator.setDuration(3000)
                valueAnimator.addUpdateListener { animation ->
                    progressWidth = animation.animatedValue as Float
                    circle = (widthSize.toFloat()/365)*progressWidth
                    invalidate()
                }
                valueAnimator.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        progressWidth = 0f
                        if(buttonState == ButtonState.Loading){
                            buttonState = ButtonState.Loading
                        }
                    }
                })
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                progressWidth = 0f
                circle = 0f
                title = resources.getString(R.string.button_start)
                invalidate()
            }
        }
    }
    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = resources.getDimension(R.dimen.default_text_size)
    }
    init {
        title = context.getString(R.string.button_start)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            buttonStartColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            buttonLoadingColor = getColor(R.styleable.LoadingButton_buttonLoadingColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_loadingCircleColor, 0)
        }
    }
    private fun drawCircle(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(widthSize / 2 + textWidth / 2 + offSet, heightSize / 2 - textSize / 2)
        paint.color = circleColor
        canvas?.drawArc(RectF(0f, 0f, textSize, textSize), 0F, circle * 0.365f, true,  paint)
        canvas?.restore()
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = buttonStartColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
        paint.color = buttonLoadingColor
        canvas?.drawRect(0f, 0f, progressWidth, heightSize.toFloat(), paint)
        paint.color = textColor
        textWidth = paint.measureText(title)
        canvas?.drawText(title, widthSize / 2 - textWidth / 2, heightSize / 2 - (paint.descent() + paint.ascent()) / 2, paint)
        drawCircle(canvas)
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
}
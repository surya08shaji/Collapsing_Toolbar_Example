package com.example.collapsingtoolbar

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.collapsingtoolbar.utility.AppBarStateChangeListener
import com.example.collapsingtoolbar.utility.Utility
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

class MainActivity : AppCompatActivity() {

    private var mAppBarLayout: AppBarLayout? = null
    private var mToolbarTextView: TextView? = null
    private  var mTitleTextView:TextView? = null
    private var mToolBar: Toolbar? = null
    private var mAppBarStateChangeListener: AppBarStateChangeListener? = null
    private val mToolbarTextPoint = FloatArray(2)
    private val mTitleTextViewPoint = FloatArray(2)
    private var mTitleTextSize = 0f
    var menuImageView: ImageView? = null
    var isImage = true
    var collapsingToolbar: CollapsingToolbarLayout? = null
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAppBarLayout = findViewById(R.id.app_bar)
        mToolbarTextView = findViewById(R.id.toolbar_title)
        mTitleTextView = findViewById(R.id.textView_title)
        mToolBar = findViewById(R.id.menuToolbar)
        menuImageView = findViewById(R.id.menuImageView)
        collapsingToolbar = findViewById(R.id.collapsingToolbar)
        toolbar = findViewById(R.id.toolbar)

        mTitleTextSize = mTitleTextView!!.getTextSize()

        setUpToolbar()
        setUpAmazingAvatar()
        setSimpleToolbar()

        if (isImage) {
            collapsingToolbar!!.setVisibility(View.VISIBLE)
            toolbar!!.setVisibility(View.GONE)
        } else {
            collapsingToolbar!!.setVisibility(View.GONE)
            toolbar!!.setVisibility(View.VISIBLE)
        }
    }

    private fun setSimpleToolbar() {
        toolbar!!.title = "Hungry Birds"
        toolbar!!.setNavigationIcon(R.drawable.back)
        toolbar!!.setNavigationOnClickListener { finish() }
    }

    private fun setUpAmazingAvatar() {
        mAppBarStateChangeListener = object : AppBarStateChangeListener() {
            override fun onStateChanged(
                appBarLayout: AppBarLayout?,
                state: State?
            ) {
            }

            override fun onOffsetChanged(state: State?, offset: Float) {
                translationView(offset)
            }
        }
        mAppBarLayout!!.addOnOffsetChangedListener(mAppBarStateChangeListener)
    }

    private fun translationView(offset: Float) {
        menuImageView!!.alpha = 1 - offset
        val newTextSize = mTitleTextSize - (mTitleTextSize - mToolbarTextView!!.textSize) * offset
        val paint = Paint(mTitleTextView!!.paint)
        paint.textSize = newTextSize
        val newTextWidth: Float = Utility.getTextWidth(paint, mTitleTextView!!.text.toString())
        paint.textSize = mTitleTextSize
        val originTextWidth: Float = Utility.getTextWidth(paint, mTitleTextView!!.text.toString())
        val xTitleOffset = originTextWidth - newTextWidth
        val yTitleOffset = (mToolbarTextPoint[1] - mTitleTextViewPoint[1]) * offset
        mTitleTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize)
        mTitleTextView!!.translationX = xTitleOffset
        mTitleTextView!!.translationY = yTitleOffset
    }

    private fun setUpToolbar() {
        mAppBarLayout!!.requestLayout()

        setSupportActionBar(mToolBar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun resetPoints(isTextChanged: Boolean) {
        val offset: Float = mAppBarStateChangeListener!!.getCurrentOffset()
        val toolbarTextPoint = IntArray(2)
        mToolbarTextView!!.getLocationOnScreen(toolbarTextPoint)
        mToolbarTextPoint[0] = toolbarTextPoint[0].toFloat()
        mToolbarTextPoint[1] = toolbarTextPoint[1].toFloat()
        val paint = Paint(mTitleTextView!!.paint)
        val newTextWidth = Utility.getTextWidth(paint, mTitleTextView!!.text.toString())
        paint.textSize = mTitleTextSize
        val originTextWidth = Utility.getTextWidth(paint, mTitleTextView!!.text.toString())
        val titleTextViewPoint = IntArray(2)
        mTitleTextView!!.getLocationOnScreen(titleTextViewPoint)
        mTitleTextViewPoint[0] = titleTextViewPoint[0] - mTitleTextView!!.translationX
                if (mToolbarTextView!!.width > newTextWidth) (originTextWidth - newTextWidth) / 2f else 0
        mTitleTextViewPoint[1] = titleTextViewPoint[1] - mTitleTextView!!.translationY
        if (isTextChanged) {
            Handler().post { translationView(offset) }
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            return
        }
        resetPoints(false)
    }
}
package com.example.multilanguange

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager

class MainActivity8 : AppCompatActivity() {
    private var mSLideViewPager: ViewPager? = null
    private var mDotLayout: LinearLayout? = null
    private var backbtn: Button? = null
    private var nextbtn: Button? = null
    private var skipbtn: Button? = null

    private lateinit var dots: Array<TextView?>
    private var viewPagerAdapter: ViewPagerAdapter? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main8)  // Make sure this layout file matches your IDs

        // Initialize views
        backbtn = findViewById(R.id.backbtn)
        nextbtn = findViewById(R.id.nextbtn)
        skipbtn = findViewById(R.id.skipButton)
        mSLideViewPager = findViewById(R.id.slideViewPager)
        mDotLayout = findViewById(R.id.indicator_layout)

        // Initialize the ViewPager adapter
        viewPagerAdapter = ViewPagerAdapter(this)
        mSLideViewPager?.adapter = viewPagerAdapter

        // Set up indicator for the first page
        setUpindicator(0)

        // Add page change listener to ViewPager
        mSLideViewPager?.addOnPageChangeListener(viewListener)

        // Set up button listeners
        backbtn?.setOnClickListener {
            if (getitem(0) > 0) {
                mSLideViewPager?.setCurrentItem(getitem(-1), true)
            }
        }

        nextbtn?.setOnClickListener {
            if (getitem(0) < 3) {
                mSLideViewPager?.setCurrentItem(getitem(1), true)
            } else {
                startActivity(Intent(this@MainActivity8, MainActivity3::class.java))
                finish()
            }
        }

        skipbtn?.setOnClickListener {
            startActivity(Intent(this@MainActivity8, MainActivity3::class.java))
            finish()
        }
    }

    private fun setUpindicator(position: Int) {
        dots = arrayOfNulls(4)
        mDotLayout?.removeAllViews()

        for (i in dots.indices) {
            dots[i] = TextView(this).apply {
                text = Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY)
                textSize = 35f
                setTextColor(ContextCompat.getColor(this@MainActivity8, R.color.inactive))
            }
            mDotLayout?.addView(dots[i])
        }

        dots[position]?.setTextColor(ContextCompat.getColor(this, R.color.active))
    }

    // Page change listener
    private val viewListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            setUpindicator(position)
            backbtn?.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
        }
        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun getitem(i: Int): Int {
        return mSLideViewPager?.currentItem?.plus(i) ?: 0
    }
}

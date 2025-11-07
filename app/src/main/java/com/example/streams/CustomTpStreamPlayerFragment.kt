package com.example.streams

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.tpstream.player.R
import com.tpstream.player.ui.TpStreamPlayerFragment

class CustomTpStreamPlayerFragment : TpStreamPlayerFragment() {
    private var isFullscreen = false
    private var originalOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    private var backCallback: OnBackPressedCallback? = null
    
    private var originalContainerParams: ViewGroup.LayoutParams? = null
    private var originalPlayerViewParams: ViewGroup.LayoutParams? = null
    private var originalContainerBackground: android.graphics.drawable.Drawable? = null
    private var originalWindowBackground: Int? = null
    private var originalContainerPadding: IntArray? = null
    
    companion object {
        private const val KEY_IS_FULLSCREEN = "key_is_fullscreen"
        private const val KEY_ORIGINAL_ORIENTATION = "key_original_orientation"
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.post {
            if (isAdded) {
                updateFullscreenButton(isFullscreen)
            }
        }
        
        savedInstanceState?.let {
            if (it.getBoolean(KEY_IS_FULLSCREEN, false)) {
                originalOrientation = it.getInt(KEY_ORIGINAL_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                view.post {
                    if (isAdded && view.windowToken != null) {
                        val activity = requireActivity() as? AppCompatActivity ?: return@post
                        enterFullscreen(activity, restoreState = true)
                    }
                }
            }
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_FULLSCREEN, isFullscreen)
        outState.putInt(KEY_ORIGINAL_ORIENTATION, originalOrientation)
    }
    
    override fun onDestroyView() {
        try {
            ViewCompat.setOnApplyWindowInsetsListener(playerContainer, null)
        } catch (e: Exception) {
            // View might already be destroyed
        }
        clearBackHandler()
        super.onDestroyView()
    }

    override fun showFullScreen() {
        val activity = requireActivity() as? AppCompatActivity ?: return
        if (isFullscreen || !isAdded) return
        enterFullscreen(activity, restoreState = false)
    }

    override fun exitFullScreen() {
        val activity = requireActivity() as? AppCompatActivity ?: return
        if (!isFullscreen || !isAdded) return
        
        ViewCompat.setOnApplyWindowInsetsListener(playerContainer, null)
        isFullscreen = false
        
        showSystemUI(activity)
        restoreState(activity)
        updateFullscreenButton(false)
        clearBackHandler()
        notifyFullscreenChanged(false)
    }

    private fun enterFullscreen(activity: AppCompatActivity, restoreState: Boolean) {
        if (!restoreState) {
            storeState(activity)
        }
        
        prepareWindow(activity)
        resizeToFullscreen()
        applyInsetsPadding()
        setupWindowInsets()
        
        playerContainer.post {
            if (!isAdded) return@post
            hideSystemUI(activity)
            switchToLandscape(activity)
            updateFullscreenButton(true)
            registerBackHandler(activity)
            notifyFullscreenChanged(true)
            isFullscreen = true
        }
    }

    private fun storeState(activity: AppCompatActivity) {
        if (originalContainerParams != null) return
        
        originalContainerParams = FrameLayout.LayoutParams(playerContainer.layoutParams)
        originalPlayerViewParams = FrameLayout.LayoutParams(tpStreamPlayerView.layoutParams)
        originalContainerBackground = playerContainer.background
        originalWindowBackground = activity.window.statusBarColor
        originalContainerPadding = intArrayOf(
            playerContainer.paddingLeft,
            playerContainer.paddingTop,
            playerContainer.paddingRight,
            playerContainer.paddingBottom
        )
    }

    private fun restoreState(activity: AppCompatActivity) {
        originalContainerParams?.let { playerContainer.layoutParams = it }
        originalPlayerViewParams?.let { tpStreamPlayerView.layoutParams = it }
        originalContainerBackground?.let { playerContainer.background = it }
        originalWindowBackground?.let { activity.window.statusBarColor = it }
        originalContainerPadding?.let {
            playerContainer.setPadding(it[0], it[1], it[2], it[3])
        }
        
        activity.requestedOrientation = preferredFullscreenExitOrientationValue
        playerContainer.requestLayout()
    }

    private fun resizeToFullscreen() {
        playerContainer.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        tpStreamPlayerView.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        playerContainer.setBackgroundColor(Color.BLACK)
        playerContainer.bringToFront()
    }

    private fun applyInsetsPadding() {
        val insets = ViewCompat.getRootWindowInsets(playerContainer)
        val bottomPadding = insets?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())?.bottom
            ?: getNavigationBarHeight()
        
        playerContainer.setPadding(0, 0, 0, bottomPadding)
        playerContainer.clipToPadding = true
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(playerContainer) { view, insets ->
            val displayInsets = insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, 0, 0, displayInsets.bottom)
            (view as? ViewGroup)?.clipToPadding = true
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(playerContainer)
    }

    private fun getNavigationBarHeight(): Int {
        val resources = playerContainer.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    private fun prepareWindow(activity: AppCompatActivity) {
        val window = activity.window
        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )
        }
    }

    private fun hideSystemUI(activity: AppCompatActivity) {
        activity.supportActionBar?.hide()
        val window = activity.window
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.getInsetsController(window, window.decorView)?.apply {
                hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )
        }
    }

    private fun showSystemUI(activity: AppCompatActivity) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowCompat.getInsetsController(window, window.decorView)?.show(
                WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        activity.supportActionBar?.show()
    }

    private fun switchToLandscape(activity: AppCompatActivity) {
        originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun updateFullscreenButton(isFullscreen: Boolean) {
        if (!isAdded) return
        
        val iconRes = if (isFullscreen) {
            R.drawable.ic_baseline_fullscreen_exit_24
        } else {
            R.drawable.ic_baseline_fullscreen_24
        }
        
        val fullscreenButton = tpStreamPlayerView.findViewById<ImageButton>(R.id.fullscreen)
        fullscreenButton?.apply {
            setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes))
            setOnClickListener {
                if (this@CustomTpStreamPlayerFragment.isFullscreen) {
                    exitFullScreen()
                } else {
                    showFullScreen()
                }
            }
        }
    }

    private fun registerBackHandler(activity: AppCompatActivity) {
        if (activity is ComponentActivity) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isFullscreen) exitFullScreen()
                }
            }
            activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
            backCallback = callback
        }
    }

    private fun clearBackHandler() {
        backCallback?.remove()
        backCallback = null
    }
}

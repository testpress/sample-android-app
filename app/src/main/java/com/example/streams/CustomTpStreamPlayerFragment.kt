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
        
        savedInstanceState?.let {
            val wasFullscreen = it.getBoolean(KEY_IS_FULLSCREEN, false)
            originalOrientation = it.getInt(KEY_ORIGINAL_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            
            if (wasFullscreen) {
                view.post {
                    val activity = requireActivity() as? AppCompatActivity ?: return@post
                    isFullscreen = true
                    storeOriginalState(activity)
                    prepareWindowForFullscreen(activity)
                    resizeToFullscreen()
                    applyWindowInsets()
                    hideSystemUI(activity)
                    updateFullscreenButton(true)
                    registerBackHandler(activity)
                    notifyFullscreenChanged(true)
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
        ViewCompat.setOnApplyWindowInsetsListener(container, null)
        clearBackHandler()
        super.onDestroyView()
    }

    override fun showFullScreen() {
        val activity = requireActivity() as? AppCompatActivity ?: return
        if (isFullscreen) return
        
        storeOriginalState(activity)
        prepareWindowForFullscreen(activity)
        resizeToFullscreen()
        
        container.post {
            applyWindowInsets()
            hideSystemUI(activity)
            switchToLandscape(activity)
            updateFullscreenButton(true)
            registerBackHandler(activity)
            notifyFullscreenChanged(true)
            isFullscreen = true
        }
    }

    override fun exitFullScreen() {
        val activity = requireActivity() as? AppCompatActivity ?: return
        if (!isFullscreen) return
        
        ViewCompat.setOnApplyWindowInsetsListener(container, null)
        showSystemUI(activity)
        restoreWindowBackground(activity)
        restoreOriginalState()
        restoreOrientation(activity)
        updateFullscreenButton(false)
        clearBackHandler()
        notifyFullscreenChanged(false)
        isFullscreen = false
    }

    private val container: ViewGroup
        get() = playerContainer

    private fun storeOriginalState(activity: AppCompatActivity) {
        if (originalContainerParams != null) return
        
        originalContainerParams = FrameLayout.LayoutParams(container.layoutParams)
        originalPlayerViewParams = FrameLayout.LayoutParams(tpStreamPlayerView.layoutParams)
        originalContainerBackground = container.background
        originalWindowBackground = activity.window.statusBarColor
        originalContainerPadding = intArrayOf(
            container.paddingLeft,
            container.paddingTop,
            container.paddingRight,
            container.paddingBottom
        )
    }

    private fun resizeToFullscreen() {
        setFullscreenSize(container)
        setFullscreenSize(tpStreamPlayerView)
        container.setBackgroundColor(Color.BLACK)
        container.bringToFront()
    }

    private fun setFullscreenSize(view: View) {
        view.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun restoreOriginalState() {
        originalContainerParams?.let { container.layoutParams = it }
        originalPlayerViewParams?.let { tpStreamPlayerView.layoutParams = it }
        container.background = originalContainerBackground
        
        originalContainerPadding?.let {
            container.setPadding(it[0], it[1], it[2], it[3])
        }
        
        container.requestLayout()
        tpStreamPlayerView.requestLayout()
    }

    private fun applyWindowInsets() {
        var maxBottomInset = 0
        
        ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val displayInsets = insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
            
            if (displayInsets.bottom > maxBottomInset) {
                maxBottomInset = displayInsets.bottom
            }
            
            val bottomPadding = maxOf(displayInsets.bottom, maxBottomInset)
            
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                bottomPadding
            )
            
            if (view is ViewGroup) {
                view.clipToPadding = true
                view.clipChildren = true
            }
            
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(container)
    }

    private fun prepareWindowForFullscreen(activity: AppCompatActivity) {
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
    
    private fun restoreWindowBackground(activity: AppCompatActivity) {
        originalWindowBackground?.let { activity.window.statusBarColor = it }
    }

    private fun switchToLandscape(activity: AppCompatActivity) {
        originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun restoreOrientation(activity: AppCompatActivity) {
        activity.requestedOrientation = preferredFullscreenExitOrientationValue
    }

    private fun updateFullscreenButton(isFullscreen: Boolean) {
        val iconRes = if (isFullscreen) {
            R.drawable.ic_baseline_fullscreen_exit_24
        } else {
            R.drawable.ic_baseline_fullscreen_24
        }
        
        // Note: This depends on the TPStream SDK's internal resource ID.
        // If the SDK changes this ID in a future update, this will silently fail.
        // Consider requesting a public API from the SDK maintainers for updating the button icon.
        val fullscreenButton = tpStreamPlayerView.findViewById<ImageButton>(R.id.fullscreen)
        fullscreenButton?.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), iconRes)
        )
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

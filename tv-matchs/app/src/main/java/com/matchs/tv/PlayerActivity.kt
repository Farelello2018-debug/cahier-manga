package com.matchs.tv

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {

    companion object { const val EXTRA_INDEX = "index" }

    private var player: ExoPlayer? = null
    private lateinit var banner: TextView
    private var index = 0
    private val handler = Handler(Looper.getMainLooper())
    private val hideBanner = Runnable { banner.visibility = View.GONE }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        banner = findViewById(R.id.channelBanner)
        index = intent.getIntExtra(EXTRA_INDEX, 0)
        if (ChannelStore.channels.isEmpty()) { finish(); return }
    }

    override fun onStart() {
        super.onStart()
        if (ChannelStore.channels.isEmpty()) return
        val p = ExoPlayer.Builder(this).build()
        p.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                showBanner("${current().name}\nFlux indisponible pour le moment. ▲▼ pour changer de chaîne.", sticky = true)
            }
        })
        findViewById<PlayerView>(R.id.playerView).player = p
        player = p
        play()
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }

    private fun current() = ChannelStore.channels[index]

    private fun play() {
        val p = player ?: return
        p.setMediaItem(MediaItem.fromUri(current().url))
        p.prepare()
        p.playWhenReady = true
        showBanner("${index + 1}. ${current().name}")
    }

    private fun showBanner(text: String, sticky: Boolean = false) {
        banner.text = text
        banner.visibility = View.VISIBLE
        handler.removeCallbacks(hideBanner)
        if (!sticky) handler.postDelayed(hideBanner, 4000)
    }

    private fun zap(step: Int) {
        val n = ChannelStore.channels.size
        index = ((index + step) % n + n) % n
        play()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> zap(+1)
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> zap(-1)
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_INFO ->
                showBanner("${index + 1}. ${current().name}")
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> player?.let { it.playWhenReady = !it.playWhenReady }
            else -> return super.onKeyDown(keyCode, event)
        }
        return true
    }
}

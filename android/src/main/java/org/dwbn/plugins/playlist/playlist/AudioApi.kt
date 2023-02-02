package org.dwbn.plugins.playlist.playlist

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.PowerManager
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.devbrackets.android.exomedia.AudioPlayer
import com.devbrackets.android.exomedia.listener.OnErrorListener
import com.devbrackets.android.playlistcore.manager.BasePlaylistManager
import com.google.android.exoplayer2.util.EventLogger
import org.dwbn.plugins.playlist.data.AudioTrack
import java.lang.ref.WeakReference
import java.util.concurrent.locks.ReentrantLock

class AudioApi(context: Context) : BaseMediaApi() {
    private val audioPlayer: AudioPlayer = AudioPlayer(context.applicationContext)

    private val errorListenersLock = ReentrantLock(true)
    private val errorListeners = ArrayList<WeakReference<OnErrorListener>>()

    override val isPlaying: Boolean
        get() = audioPlayer.isPlaying

    override val handlesOwnAudioFocus: Boolean
        get() = false

    override val currentPosition: Long
        get() = if (prepared) audioPlayer.currentPosition else 0

    override val duration: Long
        get() = if (prepared) audioPlayer.duration else 0

    override val bufferedPercent: Int
        get() = bufferPercent

    init {
        audioPlayer.setOnErrorListener(this)
        audioPlayer.setOnPreparedListener(this)
        audioPlayer.setOnCompletionListener(this)
        audioPlayer.setOnSeekCompletionListener(this)
        audioPlayer.setOnBufferUpdateListener(this)

        audioPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        audioPlayer.setAnalyticsListener(EventLogger(null))
    }

    override fun play() {
        audioPlayer.start()
    }

    override fun pause() {
        audioPlayer.pause()
    }

    override fun stop() {
        audioPlayer.stopPlayback()
    }

    override fun reset() {
        audioPlayer.reset()
    }

    override fun release() {
        audioPlayer.release()
    }

    override fun setVolume(@FloatRange(from = 0.0, to = 1.0) left: Float, @FloatRange(from = 0.0, to = 1.0) right: Float) {
        audioPlayer.setVolume(left, right)
    }

    fun setMute(isMuted: Boolean) {
        if(isMuted) audioPlayer.setVolume(0.0F,0.0F) else audioPlayer.setVolume(1.0F,1.0F)
    }

    override fun seekTo(@IntRange(from = 0L) milliseconds: Long) {
        audioPlayer.seekTo(milliseconds.toInt().toLong())
    }

    override fun handlesItem(item: AudioTrack): Boolean {
        return item.mediaType == BasePlaylistManager.AUDIO
    }

    fun setPlaybackSpeed(@FloatRange(from = 0.0, to = 1.0) speed: Float) {
        audioPlayer.playbackSpeed = speed
    }
    override fun playItem(item: AudioTrack) {
        try {
            prepared = false
            bufferPercent = 0
            audioPlayer.setDataSource(Uri.parse(if (item.downloaded) item.downloadedMediaUri else item.mediaUrl))
            audioPlayer.prepareAsync()
        } catch (e: Exception) {
            //Purposefully left blank
        }
    }

    fun addErrorListener(listener: OnErrorListener) {
        errorListenersLock.lock()
        errorListeners.add(WeakReference<OnErrorListener>(listener))
        errorListenersLock.unlock()
    }
}

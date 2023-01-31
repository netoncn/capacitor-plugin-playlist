package org.dwbn.plugins.playlist

import android.app.Application
import com.devbrackets.android.exomedia.ExoMedia
import org.dwbn.plugins.playlist.manager.PlaylistManager
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.TransferListener
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import okhttp3.OkHttpClient
import java.io.File

class App : Application() {
    private lateinit var _playlistManager: PlaylistManager;
    val playlistManager get() = _playlistManager

    fun resetPlaylistManager() {
        _playlistManager = PlaylistManager(this)
    }

    override fun onCreate() {
        resetPlaylistManager()
        super.onCreate()

        configureExoMedia()
    }

    private fun configureExoMedia() {
        // Registers the media sources to use the OkHttp client instead of the standard Apache one
        // Note: the OkHttpDataSourceFactory can be found in the ExoPlayer extension library `extension-okhttp`
        ExoMedia.setDataSourceFactoryProvider(object : ExoMedia.DataSourceFactoryProvider {
            private var instance: DataSource.Factory? = null

            override fun provide(
                userAgent: String,
                listener: TransferListener?
            ): DataSource.Factory {
                if (instance == null) {
                    //val info = JSONObject()
                    // info.put("isFakeUserAgent", isFakeUserAgent)

                    // println("teste")                    

                    // Updates the network data source to use the OKHttp implementation
                    // val upstreamFactory =
                    val userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1"

                    instance = OkHttpDataSourceFactory(OkHttpClient(), userAgent, listener)

                    // REMOVE CACHE - PLUGIN JUST USED FOR RADIO
                    // Adds a cache around the upstreamFactory
                    // val cache = SimpleCache(
                    //     File(cacheDir, "ExoMediaCache"),
                    //     LeastRecentlyUsedCacheEvictor((50 * 1024 * 1024).toLong())
                    // )
                    // instance = CacheDataSourceFactory(
                    //     cache,
                    //     upstreamFactory,
                    //     CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
                    // )
                }

                return instance!!
            }
        })
    }
}

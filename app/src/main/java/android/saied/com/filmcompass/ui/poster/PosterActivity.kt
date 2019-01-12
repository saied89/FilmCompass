package android.saied.com.filmcompass.ui.poster

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.saied.com.filmcompass.R
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_poster.*
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE



class PosterActivity : AppCompatActivity() {

    val posterUrl: String by lazy {
        intent.getStringExtra(POSTER_URL_EXTRA_TAG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poster)
        photoView.setPhotoUri(Uri.parse(posterUrl))
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    companion object {
        private val POSTER_URL_EXTRA_TAG = "POSTER_URL_EXTRA_TAG"

        fun launchPosterActivity(context: Context, posterUrl: String) {
            val intent = Intent(context, PosterActivity::class.java)
            intent.putExtra(POSTER_URL_EXTRA_TAG, posterUrl)

            context.startActivity(intent)
        }
    }
}

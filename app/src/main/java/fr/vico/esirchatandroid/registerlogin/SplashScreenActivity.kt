package fr.vico.esirchatandroid.registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import fr.vico.esirchatandroid.R
import fr.vico.esirchatandroid.messages.LatestMessagesActivity

class SplashScreenActivity : AppCompatActivity() {

    lateinit var handler:Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        handler = Handler()
        handler.postDelayed({

            val intent = Intent(this, LatestMessagesActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
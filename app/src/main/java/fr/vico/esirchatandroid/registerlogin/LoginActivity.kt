package fr.vico.esirchatandroid.registerlogin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import fr.vico.esirchatandroid.R
import fr.vico.esirchatandroid.messages.LatestMessagesActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        supportActionBar?.title="Log In"

        login_button.setOnClickListener {
            val email = email_login.text.toString()
            val password = password_login.text.toString()
            // Connexion to the tchat

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnCanceledListener {
                    Toast.makeText(this,"Email or password is false. Please retry", Toast.LENGTH_SHORT).show()
                }
        }
        back_to_registration.setOnClickListener{
            finish()
        }
    }
}
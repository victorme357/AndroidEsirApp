package fr.vico.esirchatandroid.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import fr.vico.esirchatandroid.R
import fr.vico.esirchatandroid.messages.LatestMessagesActivity
import fr.vico.esirchatandroid.models.User
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title="ESIR CHAT"
        register_button.setOnClickListener {
            performRegister()
        }

        already_have_account.setOnClickListener {
            // lauch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        select_photo_button.setOnClickListener{
            //Choose a new photo
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }
    var SelectedPhotoUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode==0 && resultCode == Activity.RESULT_OK && data != null  ){
            // check the image
            SelectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, SelectedPhotoUri)
            select_photo_imageview_register.setImageBitmap(bitmap)
            select_photo_button.alpha = 0f
            select_photo_button.setText("")
        }
    }

    private fun performRegister() {
        val email = email_register.text.toString()
        val name = name_register.text.toString()
        val password = password_register.text.toString()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please filled all text", Toast.LENGTH_SHORT).show()
            return
        }
        if(SelectedPhotoUri==null){
            Toast.makeText(this, "Please choose a photo", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase Authentification
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) { Toast.makeText(this, "Please enter corrects informations", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                // else ( if it's successful )
                Log.d("Main", "succesfully created user with uid : ${it.result?.user?.uid}")

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create the user ${it.message}")
                Toast.makeText(this,"Failed to create the user ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        // download the image on our firebase
        if(SelectedPhotoUri==null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(SelectedPhotoUri!!)
            .addOnSuccessListener {
                Toast.makeText(this,"Your profil was add successfully",Toast.LENGTH_SHORT).show()

                ref.downloadUrl.addOnSuccessListener {
                    saveUserToDatabase(it.toString())
                }
            }
            .addOnFailureListener{
                //do something here
            }
    }

    private fun saveUserToDatabase( uri: String ){
        //Add a user with all his information in the firebase database
        val uid = FirebaseAuth.getInstance().uid?:""// get uid of the user
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid" )

        val user = User(
            uid,
            name_register.text.toString(),
            uri
        )
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "we save user to our firebase!!")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed to add user to firebase")
            }
    }
}
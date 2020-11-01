package fr.vico.esirchatandroid.messages

import android.content.ClipData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import fr.vico.esirchatandroid.R
import fr.vico.esirchatandroid.groups.LatestGroups
import fr.vico.esirchatandroid.messages.NewMessageActivity.Companion.USER_KEY
import fr.vico.esirchatandroid.models.ChatMessage
import fr.vico.esirchatandroid.models.User
import fr.vico.esirchatandroid.registerlogin.RegisterActivity
import fr.vico.esirchatandroid.views.LatestMessageRow
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        menuOnClick()

        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        // set item click listener to adapter
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent ( this , ChatLogActivity::class.java)

            //looking for the chartpartneruser
            val row = item as LatestMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY, row.chartPartnerUser)
            startActivity(intent)
        }

        listenForLatestMessages()

        verifyUserisLoggedIn() // if user isn't connected, move it to the registerActivity

        fetchCurrentUser()

    }

    val adapter = GroupAdapter<ViewHolder>()

    private fun verifyUserisLoggedIn() {
    val uid = FirebaseAuth.getInstance().uid
    if ( uid == null ) {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }

        })
    }

    val latestmessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestmessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }}

    private fun listenForLatestMessages(){

        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        ref.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?:return
                adapter.add(LatestMessageRow(chatMessage))
                adapter.add(LatestMessageRow(chatMessage))
                adapter.add(LatestMessageRow(chatMessage))
                latestmessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue((ChatMessage::class.java)) ?:return
                latestmessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

        })
    }

    private fun menuOnClick(){

        new_message_btn.setOnClickListener {
            val intent = Intent(this, NewMessageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        log_out_btn.setOnClickListener{
            Toast.makeText(this,"SIGN OUT", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,
                RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        groupe_latest_message.setOnClickListener{
            val intent = Intent(this,LatestGroups::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}

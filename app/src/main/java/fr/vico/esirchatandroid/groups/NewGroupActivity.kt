package fr.vico.esirchatandroid.groups

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import fr.vico.esirchatandroid.R
import fr.vico.esirchatandroid.messages.ChatLogActivity
import fr.vico.esirchatandroid.messages.LatestMessagesActivity
import fr.vico.esirchatandroid.messages.NewMessageActivity
import fr.vico.esirchatandroid.models.Group
import fr.vico.esirchatandroid.models.User
import kotlinx.android.synthetic.main.activity_new_group.*
import kotlinx.android.synthetic.main.user_row_new_group.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewGroupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)

        menuNewGroupActivity()
        fetchUser()

    }
    val listusergroup = mutableListOf<User>()

    private fun fetchUser(){
            val ref = FirebaseDatabase.getInstance().getReference("/users")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val adapter = GroupAdapter<ViewHolder>()
                    snapshot.children.forEach {
                        val user = it.getValue(User::class.java)
                        if(user != null) {
                            adapter.add(
                                UserItem(
                                    user
                                )
                            )
                        }
                    }
                    adapter.setOnItemClickListener { item, view ->

                        val userItem = item as User
                        listusergroup.add(userItem)
                    }
                    recyclerview_new_group.adapter = adapter
    }

})}

    class UserItem ( val user: User): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.username_textview_new_group.text = user.username
            Picasso.get().load(user.profileImage).into(viewHolder.itemView.username_imageview_new_group)
        }
        override fun getLayout(): Int {
            return R.layout.user_row_new_group
        }
    }

    private fun menuNewGroupActivity(){

        come_back_new_group.setOnClickListener {
            val intent = Intent(this, LatestGroups::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
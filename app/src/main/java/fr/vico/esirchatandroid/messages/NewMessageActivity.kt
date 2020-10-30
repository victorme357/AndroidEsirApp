package fr.vico.esirchatandroid.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import fr.vico.esirchatandroid.R
import fr.vico.esirchatandroid.models.User
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        fetchUsers()
    }

    companion object {
        val USER_KEY = "USER KEY"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

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

                    val userItem = item as UserItem
                    val intent = Intent( view.context ,ChatLogActivity::class.java)
                    intent.putExtra( USER_KEY , userItem.user)
                    startActivity(intent)
                    finish()
                }

                recyclerview_new_message.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    class UserItem ( val user: User): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.username_textview_new_message.text = user.username
            Picasso.get().load(user.profileImage).into(viewHolder.itemView.username_imageview_new_message)
        }
        override fun getLayout(): Int {
            return R.layout.user_row_new_message
        }
    }


}

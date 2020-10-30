package fr.vico.esirchatandroid.views

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import fr.vico.esirchatandroid.R
import fr.vico.esirchatandroid.models.ChatMessage
import fr.vico.esirchatandroid.models.User
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestMessageRow( val chatMessage: ChatMessage ) : Item<ViewHolder>() {
    var chartPartnerUser: User? = null
    override fun getLayout(): Int {
        return R.layout.latest_messages_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val chatPartnerId : String
        if (chatMessage.fromid == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toid
        } else {
            chatPartnerId = chatMessage.fromid
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chartPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.name_textview_latest_message.text = chartPartnerUser?.username

                val targetImageView = viewHolder.itemView.image_latest_messages_row
                Picasso.get().load(chartPartnerUser?.profileImage).into(targetImageView)
            }

        })

        viewHolder.itemView.message_textview_latest_message.text = chatMessage.text
    }

}
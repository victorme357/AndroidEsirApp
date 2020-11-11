package fr.vico.esirchatandroid.views

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import fr.vico.esirchatandroid.R
import fr.vico.esirchatandroid.models.ChatMessage
import fr.vico.esirchatandroid.models.Group
import fr.vico.esirchatandroid.models.User
import kotlinx.android.synthetic.main.latest_group_row.view.*
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestGroupRow( val group: Group ) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.latest_group_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.text_view_groupname.text = group.groupname
    }
}


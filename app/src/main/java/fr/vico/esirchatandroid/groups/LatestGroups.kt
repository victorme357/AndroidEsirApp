package fr.vico.esirchatandroid.groups

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import fr.vico.esirchatandroid.R
import fr.vico.esirchatandroid.messages.ChatLogActivity
import fr.vico.esirchatandroid.messages.LatestMessagesActivity
import fr.vico.esirchatandroid.messages.LatestMessagesActivity.Companion.currentUser
import fr.vico.esirchatandroid.messages.NewMessageActivity
import fr.vico.esirchatandroid.models.ChatMessage
import fr.vico.esirchatandroid.models.Group
import fr.vico.esirchatandroid.models.User
import fr.vico.esirchatandroid.views.LatestGroupRow
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_latest_groups.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.latest_group_row.*


class LatestGroups : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_groups)

        recycler_show_groups.adapter = show_group_adapter
        recyclerview_group_message.adapter = group_message_adapter
        listenForMyLatestGroups()
        menuLatestGroup()


        send_btn_message_group.setOnClickListener {
            performSendMessageGroup()
        }

        show_group_adapter.setOnItemClickListener { item, view ->
            if(group_message_adapter !==null){ group_message_adapter.clear() }

            val row = item as LatestGroupRow
            actualgroup = row.group
            listenforGroupMessages(row.group)
            nomdugroupe.text = actualgroup.groupname

        }
    }
    var actualgroup = Group()
    val show_group_adapter = GroupAdapter<ViewHolder>()
    val group_message_adapter = GroupAdapter<ViewHolder>()
    val latestGroupsMap = HashMap<String, Group>()
    val uid = FirebaseAuth.getInstance().uid


    private fun refreshRecyclerViewGroups(){
        show_group_adapter.clear()
        latestGroupsMap.values.forEach {
            show_group_adapter.add(LatestGroupRow(it))
        }}

    private fun listenForMyLatestGroups(){

        val ref = FirebaseDatabase.getInstance().getReference("/groups")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val group = snapshot.getValue(Group::class.java) ?:return
                group.users.forEach {
                    if( it.uid == FirebaseAuth.getInstance().uid)
                        latestGroupsMap[snapshot.key!!] = group
                        refreshRecyclerViewGroups()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val group = snapshot.getValue(Group::class.java) ?:return
                group.users.forEach {
                    if( it.uid == FirebaseAuth.getInstance().uid)
                        latestGroupsMap[snapshot.key!!] = group
                        refreshRecyclerViewGroups()
            }}
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }
    private fun listenforGroupMessages(group: Group){
        val ref = FirebaseDatabase.getInstance().getReference("/groups-messages/${group.uid}")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                val currentUID = FirebaseAuth.getInstance().uid
                if (chatMessage != null) {
                    group.users.forEach {
                        if ((it.uid == chatMessage.fromid) && (chatMessage.fromid != currentUID))
                            group_message_adapter.add(ChatToItem(chatMessage.text, it))
                    }
                    if (chatMessage.fromid == currentUID) {
                        group_message_adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    }
                }

                    recyclerview_group_message.scrollToPosition(group_message_adapter.itemCount - 1)

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
    }

    // ListenforGroupMessage
    private fun performSendMessageGroup() {
        // send a message to firebase database
        val text = edittext_message_group.text.toString()
        val fromId = FirebaseAuth.getInstance().uid

        if (fromId == null) return
            val reference = FirebaseDatabase.getInstance()
                .getReference("/groups-messages/${actualgroup.uid}").push()

            val chatMessage = ChatMessage(
                id = reference.key!!,
                text = text,
                fromid = fromId,
                toid = actualgroup.uid,
                timestamp = System.currentTimeMillis() / 1000
            )
            reference.setValue(chatMessage)
                .addOnSuccessListener {
                    edittext_message_group.text.clear()
                    recyclerview_group_message.scrollToPosition(group_message_adapter.itemCount - 1)
        }
    }

    class ChatFromItem (val text:String, val user: User): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textView_from_row.text = text
            val url = user.profileImage
            val targetImageView = viewHolder.itemView.ImageView_Chat_from_row
            Picasso.get().load(url).into(targetImageView)
        }
        override fun getLayout(): Int {
            return R.layout.chat_from_row
        }

    }
    class ChatToItem(val text:String , val user: User): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textView_to_row.text = text
            val url = user.profileImage
            val targetImageView = viewHolder.itemView.ImageView_Chat_to_row
            //load our user item
            Picasso.get().load(url).into(targetImageView)
        }
        override fun getLayout(): Int {
            return R.layout.chat_to_row
        }

    }

    private fun menuLatestGroup() {
        // onClick of the menu
        group_arrow_come_back.setOnClickListener {
            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        add_group_btn.setOnClickListener{
            val intent = Intent(this, NewGroupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}
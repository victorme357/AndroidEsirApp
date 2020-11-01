package fr.vico.esirchatandroid.groups

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import fr.vico.esirchatandroid.R
import fr.vico.esirchatandroid.messages.LatestMessagesActivity
import fr.vico.esirchatandroid.models.ChatMessage
import fr.vico.esirchatandroid.models.Group
import fr.vico.esirchatandroid.models.User
import fr.vico.esirchatandroid.views.LatestGroupRow
import fr.vico.esirchatandroid.views.LatestMessageRow
import kotlinx.android.synthetic.main.activity_latest_groups.*

class LatestGroups : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_groups)

        recycler_show_groups.adapter = show_group_adapter
        recyclerview_group_message.adapter = group_message_adapter
        listenForMyLatestGroups()
        menuLatestGroup()
    }

    val show_group_adapter = GroupAdapter<ViewHolder>()
    val group_message_adapter = GroupAdapter<ViewHolder>()
    val latestGroupsMap = HashMap<String, Group>()

    private fun refreshRecyclerViewGroups(){
        show_group_adapter.clear()
        latestGroupsMap.values.forEach {
            show_group_adapter.add(LatestGroupRow(it))
        }}

    private fun listenForMyLatestGroups(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/groups")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val group = snapshot.getValue(Group::class.java) ?:return
                latestGroupsMap[snapshot.key!!] = group
                refreshRecyclerViewGroups()
            }
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val group = snapshot.getValue(Group::class.java) ?:return
                latestGroupsMap[snapshot.key!!] = group
                refreshRecyclerViewGroups()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

        })
    }

    // ListenforGroupMessage



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
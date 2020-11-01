package fr.vico.esirchatandroid.groups

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.vico.esirchatandroid.R
import fr.vico.esirchatandroid.messages.LatestMessagesActivity
import kotlinx.android.synthetic.main.activity_latest_groups.*

class LatestGroups : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_groups)

        menuLatestGroup()
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
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.letschat.*
import com.google.firebase.auth.FirebaseAuth

class message_adapter(context: Context, list: ArrayList<message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val THE_FIRST_VIEW = 1
        const val THE_SECOND_VIEW = 2
    }

    private val yourContext: Context = context
    var list: ArrayList<message> = list

    private inner class GfgViewOne(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.sentId)
        fun bind(position: Int) {
            val recyclerViewModel = list[position]
            message.text = recyclerViewModel.message

        }
    }

    private inner class View2ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.recieveId)
        fun bind(position: Int) {
            val recyclerViewModel = list[position]
            message.text = recyclerViewModel.message

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == THE_FIRST_VIEW) {
            return GfgViewOne(
                LayoutInflater.from(yourContext).inflate(R.layout.sent_message, parent, false)
            )
        }
        return View2ViewHolder(
            LayoutInflater.from(yourContext).inflate(R.layout.receive_message, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list[position].senderId?.equals(FirebaseAuth.getInstance().currentUser?.uid) == true) {
            (holder as GfgViewOne).bind(position)
        } else {
            (holder as View2ViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(list[position].senderId?.equals(FirebaseAuth.getInstance().currentUser?.uid) == true) {
            return THE_FIRST_VIEW
        }
        else{
            return THE_SECOND_VIEW
        }
    }
}

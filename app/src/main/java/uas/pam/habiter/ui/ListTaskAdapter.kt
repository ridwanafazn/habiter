package uas.pam.habiter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uas.pam.habiter.R
import uas.pam.habiter.model.DeleteResponse
import uas.pam.habiter.model.Task
import uas.pam.habiter.network.ApiClient

class ListTaskAdapter(
    private val listTask: List<Task>,
    private val taskListener: OnTaskInteractionListener,

    //    private val deletedListener: OnTaskDeletedListener,
//    private val updatedListener: OnTaskUpdatedListener
) :
    RecyclerView.Adapter<ListTaskAdapter.ListTaskViewHolder>() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    interface OnTaskInteractionListener {
        fun onTaskDeleted(position: Int)
        fun onTaskUpdated(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListTaskAdapter.ListTaskViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.task_layout, parent, false)
        return ListTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListTaskAdapter.ListTaskViewHolder, position: Int) {
        val task = listTask[position]

        holder.title.text = task.title
        holder.label.text = task.label
        holder.label.visibility = if(task.label?.isEmpty() == true) View.GONE else View.VISIBLE
        holder.updateBtn.visibility = if (task.status.equals("done")) View.GONE else View.VISIBLE
        holder.deleteBtn.setOnClickListener {
            holder.deleteTask(task._id, position, taskListener)
        }
        holder.updateBtn.setOnClickListener {
            val firebaseUser = firebaseAuth.currentUser
            var requestBody : Task = Task(
                _id = task._id,
                userId = firebaseUser?.uid,
                title = task.title,
                label = task.label,
                status = "done"
            )
            holder.updateTask(task._id, position, taskListener, requestBody)
        }
    }

    override fun getItemCount(): Int {
        return listTask.size
    }

    class ListTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = itemView.findViewById(R.id.title_task)
        val label: TextView = itemView.findViewById(R.id.label_task)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.button_delete)
        val updateBtn: ImageButton = itemView.findViewById(R.id.button_update)

        fun deleteTask(taskId: String?, position: Int, listener: OnTaskInteractionListener) {
            val context = itemView.context
            if (taskId!!.isNotEmpty()) {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                if (firebaseUser != null) {
                    val call: Call<DeleteResponse> =
                        ApiClient.apiService.deleteTask(firebaseUser.uid, taskId)
                    call.enqueue(object : Callback<DeleteResponse> {
                        override fun onResponse(
                            call: Call<DeleteResponse>,
                            response: Response<DeleteResponse>
                        ) {
                            when (response.code()) {
                                200 -> {
                                    Toast.makeText(
                                        context,
                                        "Task deleted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    (itemView.context as? AppCompatActivity)?.runOnUiThread {
                                        (itemView.context as? AppCompatActivity)?.findViewById<RecyclerView>(
                                            R.id.list_task_container
                                        )?.adapter?.notifyItemRemoved(position)
                                    }
                                    listener.onTaskDeleted(position)
                                }

                                404 -> {
                                    Toast.makeText(
                                        context,
                                        "Data not found, Try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                500 -> {
                                    Toast.makeText(
                                        context,
                                        "Internal server error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {
                                    Toast.makeText(
                                        context,
                                        "${response.code()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                            Toast.makeText(
                                context,
                                "Error when deleting data, please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            } else {
                Toast.makeText(context, "Hmm, Try to contact the developer", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        fun updateTask(taskId: String?, position: Int, listener: OnTaskInteractionListener, requestBody: Task) {
            val context = itemView.context
            if (taskId!!.isNotEmpty()) {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                if (firebaseUser != null) {
                    val call: Call<Task> =
                        ApiClient.apiService.updateTask(firebaseUser.uid, taskId, requestBody)
                    call.enqueue(object : Callback<Task> {
                        override fun onResponse(call: Call<Task>, response: Response<Task>) {
                            when (response.code()) {
                                200 -> {
                                    Toast.makeText(
                                        context,
                                        "Task updated successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    (itemView.context as? AppCompatActivity)?.runOnUiThread {
                                        (itemView.context as? AppCompatActivity)?.findViewById<RecyclerView>(
                                            R.id.list_task_container
                                        )?.adapter?.notifyItemRemoved(position)
                                    }
                                    listener.onTaskDeleted(position)
                                }

                                404 -> {
                                    Toast.makeText(
                                        context,
                                        "Data not found, Try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                500 -> {
                                    Toast.makeText(
                                        context,
                                        "Internal server error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {
                                    Toast.makeText(
                                        context,
                                        "${response.code()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<Task>, t: Throwable) {
                            Toast.makeText(
                                context,
                                "Error when deleting data, please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            } else {
                Toast.makeText(context, "Hmm, Try to contact the developer", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}

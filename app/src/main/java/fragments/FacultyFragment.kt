package fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listlab.MainActivity
import com.example.listlab.R
import com.example.listlab.databinding.FragmentFacultyBinding
import data.Faculty
import data.NameOfFragment
import interfaces.MainActivityCallbacks
import view_models.FacultyViewModel

class FacultyFragment : Fragment(), MainActivity.Edit {

    companion object {
        private var INSTANCE: FacultyFragment? = null

        fun getInstance(): FacultyFragment {
            if (INSTANCE == null) INSTANCE = FacultyFragment()
            return INSTANCE ?: throw Exception("FacultyFragment не создан")
        }
    }

    private lateinit var _binding: FragmentFacultyBinding
    val binding
        get() = _binding

    private lateinit var viewModel: FacultyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacultyBinding.inflate(inflater, container, false)

        binding.rvFaculty.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FacultyViewModel::class.java)
        viewModel = ViewModelProvider(this).get(FacultyViewModel::class.java)
        viewModel.facultyList.observe(viewLifecycleOwner) {
            binding.rvFaculty.adapter = FacultyAdapter(it ?: emptyList())
        }
    }

    private inner class FacultyAdapter(private val items: List<Faculty>) :
        RecyclerView.Adapter<FacultyAdapter.ItemHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FacultyAdapter.ItemHolder {
            val view = layoutInflater.inflate(R.layout.faculty_element, parent, false)
            return ItemHolder(view)
        }

        override fun getItemCount(): Int = items.size
        override fun onBindViewHolder(holder: FacultyAdapter.ItemHolder, position: Int) {
            holder.bind(viewModel.facultyList.value!![position])
        }
        private var lastView: View?=null

        private fun updateCurrentView(view: View){

            lastView?.findViewById<ConstraintLayout>(R.id.clFacultyElement)?.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.white))
            lastView?.findViewById<TextView>(R.id.tvFacultyName)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.black))

            view.findViewById<ConstraintLayout>(R.id.clFacultyElement)?.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.selected_element))
            view.findViewById<TextView>(R.id.tvFacultyName)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.white))


            lastView = view
        }

        private inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
            private lateinit var faculty: Faculty

            fun bind(faculty: Faculty) {
                this.faculty = faculty
                if(faculty==viewModel.faculty)
                    updateCurrentView(itemView)
                val tv = itemView.findViewById<TextView>(R.id.tvFacultyName)
                tv.text = faculty.name

                val cl = itemView.findViewById<ConstraintLayout>(R.id.clFacultyElement)
                cl.setOnClickListener{
                    viewModel.setCurrentFaculty(faculty)
                    updateCurrentView(itemView)
                }
                cl.setOnLongClickListener{
                    viewModel.setCurrentFaculty(faculty)
                    updateCurrentView(itemView)
                    mainActivity?.showFragment(NameOfFragment.GROUP)
                    true


                }
            }
        }
    }

    override fun append() {
        editFaculty()
    }

    override fun update() {
        editFaculty(viewModel?.faculty?.name ?: "")
    }

    override fun delete() {
        deleteDialog()
    }

    private fun deleteDialog()
    {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление")
            .setMessage("Вы действительно хотите удалить факультет ${viewModel.faculty?.name ?: "" }?")
            .setPositiveButton("Да"){ _, _ ->
                viewModel.deleteFaculty()
            }
            .setNegativeButton("Нет", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun editFaculty(facltyName: String = "") {
        val mDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_string, null)
        val messageText = mDialogView.findViewById<TextView>(R.id.tvInfo1)
        val inputString = mDialogView.findViewById<EditText>(R.id.editString)
        inputString.setText(facltyName)
        messageText.text = "Укажите наименование факультета"



        AlertDialog.Builder(requireContext())
            .setTitle("Изменение данных")
            .setView(mDialogView)
            .setPositiveButton("подтверждаю") { _, _ ->
                if (inputString.text.isNotBlank()) {
                    if (facltyName.isBlank())
                        viewModel.appendFaculty(inputString.text.toString())
                    else
                        viewModel.updateFaculty(inputString.text.toString())
                }
            }
            .setNegativeButton("отмена", null)
            .setCancelable(true)
            .create()
            .show()

    }

    var mainActivity:MainActivityCallbacks? = null
    override fun onAttach(context: Context){
        mainActivity = (context as MainActivityCallbacks)
        mainActivity?.newTitle("Список факультетов")
        super.onAttach(context)
    }
}

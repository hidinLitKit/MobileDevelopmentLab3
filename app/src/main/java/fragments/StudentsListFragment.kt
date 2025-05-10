package fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listlab.R
import com.example.listlab.databinding.FragmentStudentsListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.Group
import data.NameOfFragment
import data.Student
import interfaces.MainActivityCallbacks
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import view_models.StudentsListViewModel

private const val ARG_PARAM = "group_param"

class StudentsListFragment : Fragment() {


    companion object {
        @JvmStatic
        fun newInstance(group: Group) = StudentsListFragment().apply{
            arguments = Bundle().apply {
                putString(ARG_PARAM, Gson().toJson(group))
            }
        }
    }

    private lateinit var group: Group

    private lateinit var viewModel: StudentsListViewModel

    private lateinit var _binding: FragmentStudentsListBinding
    val binding
        get() = _binding


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val param1= it.getString(ARG_PARAM)
            if (param1==null)
                group = Group()
            else{
                val paramType = object :TypeToken<Group>(){}.type
                group = Gson().fromJson(param1,paramType)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentsListBinding.inflate(inflater, container, false)
        binding.rvStudents.layoutManager=
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(StudentsListViewModel::class.java)
        viewModel.studentList.observe(viewLifecycleOwner){
            binding.rvStudents.adapter =
                StudentAdapter(it.filter {it.groupID == group!!.id})
        }
        binding.fabNewStudent.setOnClickListener{
            editStudent(Student().apply { groupID = group!!.id })
        }
    }

    private fun deleteDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление")
            .setMessage("Вы действительно хотите удалить ${viewModel.student?.shortName ?: ""}?")
            .setPositiveButton("Да"){ _, _ ->
                viewModel.deleteStudent()
            }
    }

    private fun editStudent(student: Student){
        (requireActivity() as MainActivityCallbacks).showFragment(NameOfFragment.STUDENT, student)
        (requireActivity() as MainActivityCallbacks).newTitle("Группа ${group!!.name}")
    }

    private inner class StudentAdapter(private val items: List<Student>)
        : RecyclerView.Adapter<StudentAdapter.ItemHolder>(){
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): StudentAdapter.ItemHolder {
            val view = layoutInflater.inflate(R.layout.element_student_list, parent,false)
            return ItemHolder(view)
        }

        override fun onBindViewHolder(holder: StudentAdapter.ItemHolder, position: Int) {
            holder.bind(items[position])
        }
        override fun getItemCount(): Int = items.size

        private var lastView: View? = null

        private fun updateCurrentView(view: View){
            val ll = lastView?.findViewById<LinearLayout>(R.id.llStudentButtons)
            ll?.visibility= View.GONE
            val ib = lastView?.findViewById<ImageButton>(R.id.ibCall)
            ib?.visibility = View.GONE

            lastView?.findViewById<ConstraintLayout>(R.id.clStudent)?.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            view.findViewById<ConstraintLayout>(R.id.clStudent).setBackgroundColor(
                ContextCompat.getColor(requireContext(),R.color.selected_element)
            )
            lastView = view
        }

        private inner class ItemHolder(view: View)
            :RecyclerView.ViewHolder(view){

                private lateinit var student: Student

                @OptIn(DelicateCoroutinesApi::class)
                fun bind(student: Student){
                    this.student = student
                    if (student == viewModel.student)
                        updateCurrentView(itemView)

                    val tv = itemView.findViewById<TextView>(R.id.tvStudentName)
                    tv.text = student.shortName
                    val cl = itemView.findViewById<ConstraintLayout>(R.id.clStudent)
                    cl.setOnClickListener{
                        viewModel.setCurrentStudent(student)
                        updateCurrentView(itemView)

                    }
                    itemView.findViewById<ImageButton>(R.id.ibEditStudent).setOnClickListener{
                        editStudent(student)
                    }

                    itemView.findViewById<ImageButton>(R.id.ibDeleteStudent).setOnClickListener{

                        AlertDialog.Builder(requireContext())
                            .setTitle("Удаление")
                            .setMessage("Вы действительно хотите удалить ${viewModel.student?.shortName ?: ""}?")
                            .setPositiveButton("Да"){ _, _ ->
                                viewModel.deleteStudent()
                            }
                            .setNegativeButton("Нет",null)
                            .setCancelable(true)
                            .create()
                            .show()
                    }

                    val llb = itemView.findViewById<LinearLayout>(R.id.llStudentButtons)
                    llb.visibility = View.GONE
                    llb?.layoutParams = llb.layoutParams.apply { this!!.width = 1 }
                    val ib = itemView.findViewById<ImageButton>(R.id.ibCall)
                    ib.visibility = View.GONE

                    cl.setOnLongClickListener{
                        cl.callOnClick()
                        llb.visibility = View.VISIBLE
                        if(student.phone.isNotBlank())
                        {
                            ib.visibility = View.VISIBLE
                        }

                        MainScope().
                                launch {
                                    val lp = llb?.layoutParams
                                    lp?.width = 1
                                    val ip = ib.layoutParams
                                    ip.width = 1
                                    while(lp?.width!! < 350){
                                        lp?.width = lp?.width!! + 35
                                        llb?.layoutParams = lp
                                        ip.width = ip.width + 10
                                        if(ib.isVisible)
                                            ib.layoutParams = ip
                                        delay(50)
                                    }
                                }
                        true
                    }

                    itemView.findViewById<ImageButton>(R.id.ibCall).setOnClickListener{
                        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) ==
                            PackageManager.PERMISSION_GRANTED){
                            val intent = Intent(Intent.ACTION_CALL, "tel:${student.phone}".toUri())
                            startActivity(intent)
                        } else {
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 2)
                        }
                    }

                }

            }
    }
}
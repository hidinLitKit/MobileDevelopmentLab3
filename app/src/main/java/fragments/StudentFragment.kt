package fragments

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.listlab.R
import com.example.listlab.databinding.FragmentStudentBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.Student
import repository.MainRepository
import view_models.StudentViewModel
import java.text.SimpleDateFormat

private const val ARG_PARAM1 = "student_param"

class StudentFragment : Fragment() {
    private lateinit var student: Student
    private lateinit var _binding: FragmentStudentBinding

    val binding
        get() = _binding

  private val viewModel: StudentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val param1 = it.getString(ARG_PARAM1)
            if(param1 == null)
                student = Student()
            else{
                val paramType = object : TypeToken<Student>() {}.type
                student = Gson().fromJson<Student>(param1,paramType)
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentBinding.inflate(inflater,container,false)

        val sexArray = resources.getStringArray(R.array.SEX)
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, sexArray)
        binding.spSex.adapter = adapter
        binding.spSex.setSelection(student.sex)
        binding.spSex.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                student.sex = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            }
        binding.cvBirthDate.setOnDateChangeListener{ view, year, month, dayOfMonth ->
                student.birthDate.time =
                    SimpleDateFormat("yyyy.MM.dd").parse("$year.${month+1}.$dayOfMonth")?.time ?: student.birthDate.time
        }
        binding.etFirstName.setText(student.firstName)
        binding.etMiddleName.setText(student.middleName)
        binding.etLastName.setText(student.lastName)
        binding.etPhone.setText(student.phone)
        binding.cvBirthDate.date = student.birthDate.time
        binding.bttCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.bttSave.setOnClickListener {
            student.lastName = binding.etLastName.text.toString()
            student.middleName = binding.etMiddleName.text.toString()
            student.firstName = binding.etFirstName.text.toString()
            student.phone = binding.etPhone.text.toString()
            MainRepository.getInstance().updateStudent(student)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
    }

    companion object{
        @JvmStatic
        fun newInstance(student: Student) =
            StudentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, Gson().toJson(student))
                }
            }
    }

}
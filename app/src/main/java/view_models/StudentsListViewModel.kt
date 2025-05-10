package view_models

import androidx.lifecycle.ViewModel
import data.Group
import data.Student
import repository.MainRepository
import java.util.Date

class StudentsListViewModel : ViewModel() {
    var studentList = MainRepository.getInstance().studentList
    private var _student: Student? = null

    val student
        get() = _student

    init {
        MainRepository.getInstance().student.observeForever{
            _student = it
        }
    }

    var group: Group? = null
    fun set_Group(group: Group) {
        this.group = group
    }

    fun deleteStudent(){
        if(student!=null)
                MainRepository.getInstance().deleteStudent(student!!)
    }

    fun appendStudent(
        lastName: String,
        firstName: String,
        middleName: String,
        birthDate: Date,
        phone: String
    ){
        val student = Student()
        student.lastName = lastName
        student.firstName = firstName
        student.middleName = middleName
        student.birthDate = birthDate
        student.phone = phone
        student.groupID = group!!.id
        MainRepository.getInstance().newStudent(student)
    }

    fun updateStudent(
        lastName: String,
        firstName: String,
        middleName: String,
        birthDate: Date,
        phone: String
    ){
        if(_student != null)
        {
            _student!!.lastName = lastName
            _student!!.firstName = firstName
            _student!!.middleName = middleName
            _student!!.birthDate = birthDate
            _student!!.phone = phone

            MainRepository.getInstance().updateStudent(_student!!)
        }
    }

    fun setCurrentStudent(student: Student){
        MainRepository.getInstance().setCurrentStudent(student)
    }


}
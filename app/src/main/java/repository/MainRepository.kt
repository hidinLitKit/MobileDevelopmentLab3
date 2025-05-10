package repository

import android.icu.text.Transliterator.Position
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.preference.PreferenceManager
import com.example.listlab.AppList
import com.example.listlab.R
import data.Faculty
import data.FacultyList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.Group
import data.Student
import database.MyDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainRepository private constructor(){
    companion object {
        private var INSTANCE: MainRepository? = null

        fun getInstance(): MainRepository {
            if (INSTANCE == null) {
                INSTANCE = MainRepository()
            }
            return INSTANCE
                ?: throw IllegalStateException("MainRepository: Репозиторий не инициализирован")
        }
    }
 //   var listOfFaculty: MutableLiveData<FacultyList?> = MutableLiveData()

    var faculty: MutableLiveData<Faculty?> = MutableLiveData()

    private val listDB by lazy { OfflineDBRepository(MyDatabase.getDatabase(AppList.context).MyDAO())}

    var listOfFaculty: LiveData<List<Faculty>> = listDB.getFaculty()
        .asLiveData()

    private val myCoroutineScope = CoroutineScope(Dispatchers.Main)

    fun onDestroy()
    {
        myCoroutineScope.cancel()
    }

    fun saveData(){
        /*
        val context =AppList.context
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().apply{
            val gson = Gson()
            val lst = listOfFaculty.value?.items ?: listOf<Faculty>()
            val jsonString = gson.toJson(lst)
            putString(context.getString(R.string.preference_key_faculty_list), jsonString)
            apply()
        }

         */
    }
    fun loadData(){
        /*
        val context =AppList.context
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.apply {
            val jsonString = getString(context.getString(R.string.preference_key_faculty_list), null)
            if(jsonString!=null){
                val listType = object : TypeToken<List<Faculty>>() {}.type
                val tempList = Gson().fromJson<List<Faculty>>(jsonString,listType)
                val temp = FacultyList()
                temp.items = tempList.toMutableList()
                listOfFaculty.postValue(temp)
            }
        }

         */
    }

    fun addFaculty(faculty: Faculty){
        myCoroutineScope.launch {
            listDB.insertFaculty(faculty)
            setCurrentFaculty(faculty)
        }
    }

    fun updateFaculty(faculty: Faculty){
        addFaculty(faculty)
    }

    fun deleteFaculty(faculty: Faculty){
        myCoroutineScope.launch {
            listDB.deleteFaculty(faculty)
            setCurrentFaculty(0)
        }
    }

    fun setCurrentFaculty(position: Int)
    {
        /*
        if(listOfFaculty.value == null || position < 0
            || (listOfFaculty.value?.items?.size!! <= position))
            return
        setCurrentFaculty(listOfFaculty.value?.items!![position])
         */
        if (position<0 || (listOfFaculty.value?.size!!<=position))
            return
        setCurrentFaculty(listOfFaculty.value!![position])
    }

    fun setCurrentFaculty(_faculty: Faculty)
    {
        faculty.postValue(_faculty)
    }

    /*
        fun addFaculty(faculty: Faculty)
        {
            val listTmp = (listOfFaculty.value ?: FacultyList()).apply{
                items.add(faculty)
            }
            listOfFaculty.postValue(listTmp)
            setCurrentFaculty(faculty)
        }

        fun updateFaculty(faculty: Faculty)
        {
            val position = getFacultyPosition(faculty)
            if(position < 0) addFaculty(faculty)
            else
            {
                val listTmp = listOfFaculty.value!!
                listTmp.items[position]=faculty
                listOfFaculty.postValue(listTmp)
            }
        }

        fun deleteFaculty(faculty: Faculty)
        {
            val listTmp = listOfFaculty.value!!
            if(listTmp.items.remove(faculty)){
                listOfFaculty.postValue(listTmp)
            }
            setCurrentFaculty(0)
        }

        fun getFacultyPosition(faculty: Faculty): Int = listOfFaculty.value?.items?.indexOfFirst {
            it.ID == faculty.ID } ?: -1

        fun getFacultyPosition() = getFacultyPosition(faculty.value?:Faculty())


     */

    var groupList: LiveData<List<Group>> = listDB.getAllGroups().asLiveData()
    var group: MutableLiveData<Group> = MutableLiveData()
    val facultyGroups
        get() = {
            if (faculty.value != null) listDB.getFacultyGroups(faculty.value!!.id)
            else emptyList()
        }

    fun newGroup(group: Group) {
        myCoroutineScope.launch {
            listDB.insertGroup(group)
            setCurrentGroup(group)
        }
    }

    fun setCurrentGroup(_group: Group) {
        group.postValue(_group)
    }

    fun setCurrentGroup(position: Int) {
        if (groupList.value == null || position < 0 || (groupList.value?.size!! <= position))
            return
        setCurrentGroup(groupList.value!![position])
    }

    fun getGroupPosition(group: Group): Int = groupList.value?.indexOfFirst {
        it.id == group.id
    } ?: 1

    fun getGroupPosition() = getGroupPosition(group.value ?: Group())

    fun updateGroup(group: Group) {
        newGroup(group)
    }

    fun deleteGroup(group: Group) {
        myCoroutineScope.launch {
            listDB.deleteGroup(group)
        }
        setCurrentGroup(0)
    }

    var studentList: LiveData<List<Student>> = listDB.getAllStudents().asLiveData()
    var student: MutableLiveData<Student> = MutableLiveData()

    fun newStudent(student: Student) {
        myCoroutineScope.launch {
            listDB.insertStudent(student)
            setCurrentStudent(student)
        }
    }

    fun setCurrentStudent(_student: Student) {
        student.postValue(_student)
    }

    fun setCurrentStudent(position: Int) {
        if (studentList.value == null || position < 0 || (studentList.value?.size!! <= position))
            return
        setCurrentStudent(studentList.value!![position])
    }

    fun getStudentPosition(student: Student): Int = studentList.value?.indexOfFirst {
        it.id == student.id
    } ?: 1

    fun getStudentPosition() = getStudentPosition(student.value ?: Student())

//    fun updateStudent(student: Student) {
//        newStudent(student)
//    }
//

    fun updateStudent(student: Student){
        newStudent(student)
    }
    fun deleteStudent(student: Student) {
        myCoroutineScope.launch {
            listDB.deleteStudent(student)
            setCurrentStudent(0)
        }
    }


}
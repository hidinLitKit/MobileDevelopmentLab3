package view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import data.Faculty
import data.FacultyList
import repository.MainRepository

class FacultyViewModel : ViewModel() {

    var facultyList = MainRepository.getInstance().listOfFaculty

    private var _faculty: Faculty? = null

    val faculty
        get() = _faculty

    /*private  val facultyListObserver = Observer<FacultyList?>{
        list ->
            facultyList.postValue(list)
    }

     */

    init {
        //MainRepository.getInstance().listOfFaculty.observeForever(facultyListObserver)
        MainRepository.getInstance().faculty.observeForever{
            _faculty = it
        }
    }

    fun deleteFaculty(){
        if(faculty != null){
            MainRepository.getInstance().deleteFaculty(faculty!!)
        }
    }

    fun appendFaculty(name: String){
        val faculty = Faculty()
        faculty.name = name
        MainRepository.getInstance().addFaculty(faculty)
    }

    fun updateFaculty(name: String){
        if (_faculty != null){
            _faculty!!.name = name
            MainRepository.getInstance().updateFaculty(_faculty!!)
        }
    }

    fun setCurrentFaculty(faculty: Faculty){
        MainRepository.getInstance().setCurrentFaculty(faculty)
    }

}
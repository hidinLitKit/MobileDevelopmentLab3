package interfaces

import data.NameOfFragment
import data.Student
import fragments.GroupFragment

interface MainActivityCallbacks {
    fun newTitle(_title: String)
    fun showFragment(fragmentType: NameOfFragment,student: Student? = null)
}
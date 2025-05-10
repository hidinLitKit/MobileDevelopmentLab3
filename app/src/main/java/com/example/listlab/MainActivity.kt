package com.example.listlab

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import data.NameOfFragment
import data.Student
import fragments.FacultyFragment
import fragments.GroupFragment
import fragments.StudentFragment
import fragments.StudentsListFragment
import interfaces.MainActivityCallbacks
import java.util.jar.Attributes.Name

class MainActivity : AppCompatActivity(), MainActivityCallbacks {
    interface Edit{
        fun append()
        fun update()
        fun delete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this){
            if(supportFragmentManager.backStackEntryCount > 0){
                supportFragmentManager.popBackStack()
                when(activeFragment){
                    NameOfFragment.FACULTY -> {
                        finish()
                    }
                    NameOfFragment.GROUP -> {
                        activeFragment = NameOfFragment.FACULTY
                        newTitle("Список факультетов")
                    }
                    NameOfFragment.STUDENT -> {
                        activeFragment = NameOfFragment.GROUP
                    }
                }
                updateMenu(activeFragment)
            }
            else{
                finish()
            }
        }

        showFragment(NameOfFragment.FACULTY)
    }
    private var _miAppendFaculty: MenuItem? =null
    private var _miChangeFaculty: MenuItem? =null
    private var _miDeleteFaculty: MenuItem? =null
    private var _miAppendGroup:MenuItem? = null
    private var _miUpdateGroup:MenuItem? = null
    private var _miDeleteGroup:MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        _miAppendFaculty = menu?.findItem(R.id.miNewFaculty)
        _miChangeFaculty = menu?.findItem(R.id.miChangeFaculty)
        _miDeleteFaculty = menu?.findItem(R.id.miDeleteFaculty)
        _miAppendGroup = menu?.findItem(R.id.miAppendGroup)
        _miUpdateGroup = menu?.findItem(R.id.miUpdateGroup)
        _miDeleteGroup = menu?.findItem(R.id.miDeleteGroup)
        updateMenu(activeFragment)
        return true
    }

    var activeFragment: NameOfFragment = NameOfFragment.FACULTY

    private fun updateMenu(fragmentType: NameOfFragment){
        _miAppendFaculty?.isVisible = fragmentType == NameOfFragment.FACULTY
        _miChangeFaculty?.isVisible = fragmentType == NameOfFragment.FACULTY
        _miDeleteFaculty?.isVisible = fragmentType == NameOfFragment.FACULTY
        _miUpdateGroup?.isVisible = fragmentType == NameOfFragment.GROUP
        _miAppendGroup?.isVisible = fragmentType == NameOfFragment.GROUP
        _miDeleteGroup?.isVisible = fragmentType == NameOfFragment.GROUP
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.miNewFaculty ->{
                FacultyFragment.getInstance().append()
                true
            }
            R.id.miChangeFaculty ->{
                FacultyFragment.getInstance().update()
                true
            }
            R.id.miDeleteFaculty ->{
                FacultyFragment.getInstance().delete()
                true
            }
            R.id.miAppendGroup ->{
                GroupFragment.getInstance().append()
                true
            }
            R.id.miUpdateGroup ->{
                GroupFragment.getInstance().update()
                true
            }
            R.id.miDeleteGroup ->{
                GroupFragment.getInstance().delete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun newTitle(_title: String) {
        title = _title
    }

    override fun showFragment(
        fragmentType: NameOfFragment,
        student: Student?
    )
    {
        when(fragmentType)
        {
            NameOfFragment.FACULTY ->
            {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcvMain, FacultyFragment.getInstance())
                    .addToBackStack(null)
                    .commit()

            }
            NameOfFragment.GROUP ->
            {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcvMain, GroupFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
            NameOfFragment.STUDENT ->
            {
                if(student != null)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fcvMain, StudentFragment.newInstance(student))
                        .addToBackStack(null)
                        .commit()
            }

        }
        activeFragment = fragmentType
        updateMenu(fragmentType)

    }
}
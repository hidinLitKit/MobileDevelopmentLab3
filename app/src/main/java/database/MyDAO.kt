package database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import data.Faculty
import data.FacultyList
import data.Group
import data.Student
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MyDAO {

    //FACULTY

    @Query("SELECT * FROM faculties order by faculty_name")
    fun getFaculty(): Flow<List<Faculty>>

    @Insert(entity = Faculty::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaculty(faculty: Faculty)

    @Update(entity = Faculty::class)
    suspend fun updateFaculty(faculty: Faculty)

    @Insert(entity = Faculty::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFaculty(facultyList: List<Faculty>)

    @Delete(entity = Faculty::class)
    suspend fun deleteFaculty(faculty:Faculty)

    @Query("DELETE FROM faculties")
    suspend fun deleteAllFaculty()

    //GROUPS

    @Query("SELECT * FROM groups order by group_name")
    fun getAllGroups(): Flow<List<Group>>

    @Query("select * from groups where faculty_id=:facultyID")
    fun getFacultyGroups(facultyID: UUID): List<Group>

    @Insert(entity = Group::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Update(entity = Group::class)
    suspend fun updateGroup(group: Group)

    @Insert(entity = Group::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGroup(facultyList: List<Group>)

    @Delete(entity = Group::class)
    suspend fun deleteGroup(group:Group)

    @Query("DELETE FROM groups")
    suspend fun deleteAllGroups()

    //STUDENTS

    @Query("SELECT * FROM students order by lastName")
    fun getAllStudents(): Flow<List<Student>>

    @Query("select * from students where group_id=:groupID")
    fun getGroupStudents(groupID: UUID): Flow<List<Student>>


    @Insert(entity = Student::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Update(entity = Student::class)
    suspend fun updateStudent(student: Student)

    @Insert(entity = Student::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudent(studentList: List<Student>)

    @Delete(entity = Student::class)
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()
}
package repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import data.Faculty
import data.Group
import data.Student
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface DBRepository {
    fun getFaculty(): Flow<List<Faculty>>
    suspend fun insertFaculty(faculty: Faculty)
    suspend fun updateFaculty(faculty: Faculty)
    suspend fun insertAllFaculty(facultyList: List<Faculty>)
    suspend fun deleteFaculty(faculty: Faculty)
    suspend fun deleteAllFaculty()


    fun getAllGroups(): Flow<List<Group>>
    fun getFacultyGroups(facultyID: UUID): List<Group>
    suspend fun updateGroup(group: Group)
    suspend fun insertGroup(group: Group)
    suspend fun deleteGroup(group: Group)
    suspend fun deleteAllGroups()


    fun getAllStudents(): Flow<List<Student>>
    suspend fun getGroupStudents(groupID: UUID): Flow<List<Student>>
    suspend fun insertStudent(student: Student)
    suspend fun deleteStudent(student: Student)
    suspend fun deleteAllStudents()

}
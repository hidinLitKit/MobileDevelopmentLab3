package repository

import data.Faculty
import data.Group
import data.Student
import database.MyDAO
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class OfflineDBRepository(val dao: MyDAO ) :DBRepository{
        override fun getFaculty(): Flow<List<Faculty>> = dao.getFaculty()
        override suspend fun insertFaculty(faculty: Faculty) = dao.insertFaculty(faculty)
        override suspend fun updateFaculty(faculty: Faculty) = dao.updateFaculty(faculty)
        override suspend fun insertAllFaculty(facultyList: List<Faculty>) = dao.insertAllFaculty(facultyList)
        override  suspend fun deleteFaculty(faculty: Faculty) = dao.deleteFaculty(faculty)
        override suspend fun deleteAllFaculty() = dao.deleteAllFaculty()

        override fun getAllGroups() = dao.getAllGroups()
        override fun getFacultyGroups(facultyID: UUID) = dao.getFacultyGroups(facultyID)
        override suspend fun updateGroup(group: Group) = dao.updateGroup(group)
        override suspend fun insertGroup(group: Group) = dao.insertGroup(group)
        override suspend fun deleteGroup(group: Group) = dao.deleteGroup(group)
        override suspend fun deleteAllGroups() = dao.deleteAllGroups()


        override fun getAllStudents() = dao.getAllStudents()
        override suspend fun getGroupStudents(groupID: UUID) = dao.getGroupStudents(groupID)
        override suspend fun insertStudent(student: Student) = dao.insertStudent(student)
        override suspend fun deleteStudent(student: Student) = dao.deleteStudent(student)
        override suspend fun deleteAllStudents() = dao.deleteAllStudents()
}

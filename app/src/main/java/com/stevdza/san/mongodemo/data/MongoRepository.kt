package com.stevdza.san.mongodemo.data

import com.stevdza.san.mongodemo.model.Access
import com.stevdza.san.mongodemo.model.AssociateParent
import com.stevdza.san.mongodemo.model.AttendanceStudent
import com.stevdza.san.mongodemo.model.ClassArms
import com.stevdza.san.mongodemo.model.ClassName
import com.stevdza.san.mongodemo.model.CompanyProfile
import com.stevdza.san.mongodemo.model.Movement
import com.stevdza.san.mongodemo.model.Parent
import com.stevdza.san.mongodemo.model.Session
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.TeacherAbsentAttendance
import com.stevdza.san.mongodemo.model.StaffAttendance
import com.stevdza.san.mongodemo.model.TeacherProfile
import com.stevdza.san.mongodemo.model.Term
import com.stevdza.san.mongodemo.model.TimeInOut
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface MongoRepository {

    fun configureTheRealm()


    // Staff Movement
    fun getCompanyData(): Flow<List<CompanyProfile>>
    fun getCompanyWithID(_id: ObjectId): CompanyProfile
    fun getCompanyWithSchoolName(schoolName: String): CompanyProfile
    suspend fun insertCompanyProfile(companyProfile: CompanyProfile)
    suspend fun updateCompany(companyProfile: CompanyProfile)
    suspend fun deleteCompany(id: ObjectId)
    suspend fun deleteCompanyAll(term: List<CompanyProfile>)


    // Staff Movement
    fun staffMovementData(): Flow<List<Movement>>
    fun getStaffMovementWithID(_id: ObjectId): Movement
    suspend fun insertStaffMovement(movement: Movement)
    suspend fun updateStaffMovement(movement: Movement)
    suspend fun deleteStaffMovement(id: ObjectId)
    suspend fun deleteMovementAll(term: List<Movement>)



    // Time In and Out Data
    fun getTimeInOutData(): Flow<List<TimeInOut>>
    fun getTimeINOutWithID(_id: ObjectId): TimeInOut
    suspend fun insertTimeInOut(timeInOut: TimeInOut)
    suspend fun updateTimeIn(timeInOut: TimeInOut)
    suspend fun updateTimeOut(timeInOut: TimeInOut)
    suspend fun deleteTimeInOut(id: ObjectId)
    suspend fun deleteTimeInOutAll(term: List<TimeInOut>)



    // Absent Teacher Data
    fun getTeacherAbsentData(): Flow<List<TeacherAbsentAttendance>>
    fun getTeacherAbsentWithID(_id: ObjectId): TeacherAbsentAttendance
    suspend fun insertTeacherAbsent(teacherAbsentAttendance: TeacherAbsentAttendance)
    suspend fun updateTeacherAbsent(teacherAbsentAttendance: TeacherAbsentAttendance)
    suspend fun deleteTeacherAbsent(id: ObjectId)
    suspend fun deleteTeacherAbsentAll(term: List<TeacherAbsentAttendance>)



    // Teacher Profile Data
    fun getTeacherData(): Flow<List<TeacherProfile>>
    fun getTeacherWithID(_id: ObjectId): TeacherProfile
    fun getTeacherWithSurname(surname: String): Flow<List<TeacherProfile>>
    suspend fun insertTeacher(teacherProfile: TeacherProfile)
    suspend fun updateTeacher(teacherProfile: TeacherProfile)
    suspend fun updateTeacherStatus(teacherProfile: TeacherProfile)
    suspend fun deleteTeacher(id: ObjectId)
    suspend fun deleteTeacherProfileAll(term: List<TeacherProfile>)



    // TeacherAttendance
    fun getTeacherAttendanceData(): Flow<List<StaffAttendance>>
    fun getTeacherAttendanceWithID(_id: ObjectId): StaffAttendance
    fun getStaffWithName(name: String): Flow<List<StaffAttendance>>
    suspend fun insertTeacherAttendance(teacherAttendance: StaffAttendance)
    suspend fun updateTeacherAttendance(teacherAttendance: StaffAttendance)
    suspend fun deleteTeacherAttendance(id: ObjectId)
    suspend fun deleteStaffAttendanceAll(term: List<StaffAttendance>)



    // Access
    fun getAccessData(): Flow<List<Access>>
    fun getAccessWithID(_id: ObjectId): Access
    suspend fun insertAccess(access: Access)
    suspend fun updateAccess(access: Access)
    suspend fun deleteAccess(id: ObjectId)
    suspend fun deleteAccessAll(term: List<Access>)



    // Parent
    fun getParentData(): Flow<List<Parent>>
    fun getParentWithID(_id: ObjectId): Parent
    fun getParentWithSurname(surname: String): Flow<List<Parent>>
    suspend fun insertParent(parent: Parent)
    suspend fun updateParent(parent: Parent)
    suspend fun deleteParent(id: ObjectId)
    suspend fun deleteParentAll(term: List<Parent>)



    // Child
    fun getStudentData(): Flow<List<StudentData>>
    fun getStudentWithID(_id: ObjectId): StudentData
    fun getStudentWithClass(cless: String): Flow<List<StudentData>>
    fun getStudentWithChildId(studentId: String): StudentData
    fun getStudentWithFamilyID(familyId: String): Flow<List<StudentData>>
    fun getStudentWithSurname(surname: String): Flow<List<StudentData>>
    suspend fun insertStudent(student: StudentData)
    suspend fun updateStudent(student: StudentData)
    suspend fun updateStudentClass(student: StudentData)
    suspend fun updateStudentStatus(student: StudentData)
    suspend fun updateStudentFamilyId(student: StudentData)
    suspend fun deleteStudent(id: ObjectId)
    suspend fun deleteStudentDataAll(term: List<StudentData>)



    // Associate Parent
    fun getAssociateParentData(): Flow<List<AssociateParent>>
    fun getAssociateParentWithID(_id: ObjectId): AssociateParent
    fun getAssociateParentWithFamilyId(familyId: String): Flow<List<AssociateParent>>
    suspend fun insertAssociateParent(associateParent: AssociateParent)
    suspend fun updateAssociateParent(associateParent: AssociateParent)
    suspend fun deleteAssociateParent(id: ObjectId)
    suspend fun deleteAssociateParentAll(term: List<AssociateParent>)




    // Class Names
    fun getClassNames(): Flow<List<ClassName>>
    fun getClassNamesWithID(_id: ObjectId): ClassName
    suspend fun insertClassNames(className: ClassName)
    suspend fun updateClassNames(className: ClassName)
    suspend fun deleteClassNames(id: ObjectId)
    suspend fun deleteClassNameAll(term: List<ClassName>)



    // Class Arms
    fun getClassArms(): Flow<List<ClassArms>>
    fun getClassArmsWithID(_id: ObjectId): ClassArms
    suspend fun insertClassArms(classArms: ClassArms)
    suspend fun updateClassArms(classArms: ClassArms)
    suspend fun deleteClassArms(id: ObjectId)
    suspend fun deleteClassArmsAll(term: List<ClassArms>)


    // Term
    fun getTerm(): Flow<List<Term>>
    fun getTermWithId(_id: ObjectId): Term
    fun getTermWithtermname(termName: String): Term
    suspend fun insertTerm(term: Term)
    suspend fun updateTerm(term: Term)
    suspend fun deleteTerm(id: ObjectId)
    suspend fun deleteSessionAll(term: List<Term>)


    // Session
    fun getSession(): Flow<List<Session>>
    fun getSessionWithID(_id: ObjectId): Session
    suspend fun insertSession(session: Session)
    suspend fun updateSession(session: Session)
    suspend fun deleteSession(id: ObjectId)
    suspend fun deleteYearAll(term: List<Session>)



    // Attendance In and Out
    fun getAttendance(): Flow<List<AttendanceStudent>>
    fun getAttendanceWithID(_id: ObjectId): AttendanceStudent
    fun getAttendanceWithFamilyID(familyId: String): Flow<List<AttendanceStudent>>
    suspend fun insertAttendance(attendanceModel: AttendanceStudent)
    suspend fun updateAttendance(attendanceModel: AttendanceStudent)
    suspend fun deleteAttendance(date: String)
    suspend fun deleteAttendanceStudentAll(term: List<AttendanceStudent>)


}
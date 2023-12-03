package com.stevdza.san.mongodemo.data

import android.util.Log
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
import com.stevdza.san.mongodemo.model.StudentIn
import com.stevdza.san.mongodemo.model.StudentOut
import com.stevdza.san.mongodemo.model.StudentStatus
import com.stevdza.san.mongodemo.model.TeacherAbsentAttendance
import com.stevdza.san.mongodemo.model.StaffAttendance
import com.stevdza.san.mongodemo.model.TeacherProfile
import com.stevdza.san.mongodemo.model.Term
import com.stevdza.san.mongodemo.model.TimeInOut
import com.stevdza.san.mongodemo.util.Constants.APP_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

object AlkhairDB : MongoRepository {
    private val app = App.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(
                user,
                setOf(
                    Access::class,
                    AssociateParent::class, TeacherProfile::class, StaffAttendance::class,
                    Parent::class, ClassName::class, ClassArms::class, Term::class, Session::class,
                    AttendanceStudent::class, StudentIn::class, StudentOut::class, StudentData::class,
                    StudentStatus::class, TimeInOut::class, TeacherAbsentAttendance::class, Movement::class,
                    CompanyProfile::class
                   ))
                .initialSubscriptions { sub ->
                    add(query = sub.query<Access>(query = "owner_id == $0", user.id))
                    add(query = sub.query<AssociateParent>(query = "owner_id == $0", user.id))
                    add(query = sub.query<Parent>(query = "owner_id == $0", user.id))
                    add(query = sub.query<ClassName>(query = "owner_id == $0", user.id))
                    add(query = sub.query<ClassArms>(query = "owner_id == $0", user.id))
                    add(query = sub.query<Term>(query = "owner_id == $0", user.id))
                    add(query = sub.query<Session>(query = "owner_id == $0", user.id))
                    add(query = sub.query<AttendanceStudent>(query = "owner_id == $0", user.id))
                    add(query = sub.query<StudentData>(query = "owner_id == $0", user.id))
                    add(query = sub.query<TeacherProfile>(query = "owner_id == $0", user.id))
                    add(query = sub.query<StaffAttendance>(query = "owner_id == $0", user.id))
                    add(query = sub.query<TimeInOut>(query = "owner_id == $0", user.id))
                    add(query = sub.query<TeacherAbsentAttendance>(query = "owner_id == $0", user.id))
                    add(query = sub.query<Movement>(query = "owner_id == $0", user.id))
                    add(query = sub.query<CompanyProfile>(query = "owner_id == $0", user.id))
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    override fun getCompanyData(): Flow<List<CompanyProfile>> {
        return realm.query<CompanyProfile>().asFlow().map { it.list }

    }

    override fun getCompanyWithID(_id: ObjectId): CompanyProfile {
        return realm.query<CompanyProfile>(query = "_id == $0", _id).first().find() as CompanyProfile
    }

    override fun getCompanyWithSchoolName(schoolName: String): CompanyProfile {
        return realm.query<CompanyProfile>(query = "_id == $0", schoolName).first().find() as CompanyProfile
    }

    override suspend fun insertCompanyProfile(companyProfile: CompanyProfile) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(companyProfile.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.e("insertCompanyProfile", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateCompany(companyProfile: CompanyProfile) {
        realm.write {
            val queriedParent = query<CompanyProfile>(query = "_id == $0", companyProfile._id).first().find()
            queriedParent?.accessPin = companyProfile.accessPin
        }
    }

    override suspend fun deleteCompany(id: ObjectId) {
        realm.write {
            val movement = query<CompanyProfile>(query = "_id == $0", id).first().find()
            try {
                movement?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteCompanyAll(term: List<CompanyProfile>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    // Movement
    override fun staffMovementData(): Flow<List<Movement>> {
        return realm.query<Movement>().asFlow().map { it.list }
    }

    override fun getStaffMovementWithID(_id: ObjectId): Movement {
        return realm.query<Movement>(query = "_id == $0", _id).first().find() as Movement
    }

    override suspend fun insertStaffMovement(movement: Movement) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(movement.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.e("insertStaffMovement", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateStaffMovement(movement: Movement) {
        realm.write {
            val queriedParent = query<Movement>(query = "_id == $0", movement._id).first().find()
            queriedParent?.timeOut = movement.timeOut
        }
    }

    override suspend fun deleteStaffMovement(id: ObjectId) {
        realm.write {
            val movement = query<Movement>(query = "_id == $0", id).first().find()
            try {
                movement?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteMovementAll(term: List<Movement>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    // Time In Out
    override fun getTimeInOutData(): Flow<List<TimeInOut>> {
        return realm.query<TimeInOut>().asFlow().map { it.list }
    }

    override fun getTimeINOutWithID(_id: ObjectId): TimeInOut {
        return realm.query<TimeInOut>(query = "_id == $0", _id).first().find() as TimeInOut
    }

    override suspend fun insertTimeInOut(timeInOut: TimeInOut) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(timeInOut.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateTimeIn(timeInOut: TimeInOut) {
        realm.write {
            val queriedParent = query<TimeInOut>(query = "_id == $0", timeInOut._id).first().find()
            queriedParent?.timeIn = timeInOut.timeIn
        }
    }

    override suspend fun updateTimeOut(timeInOut: TimeInOut) {
        realm.write {
            val queriedParent = query<TimeInOut>(query = "_id == $0", timeInOut._id).first().find()
            queriedParent?.timeOut = timeInOut.timeOut
        }
    }

    override suspend fun deleteTimeInOut(id: ObjectId) {
        realm.write {
            val timeInOut = query<TimeInOut>(query = "_id == $0", id).first().find()
            try {
                timeInOut?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteTimeInOutAll(term: List<TimeInOut>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    // Teacher Absent

    override fun getTeacherAbsentData(): Flow<List<TeacherAbsentAttendance>> {
        return realm.query<TeacherAbsentAttendance>().asFlow().map { it.list }
    }

    override fun getTeacherAbsentWithID(_id: ObjectId): TeacherAbsentAttendance {
        return realm.query<TeacherAbsentAttendance>(query = "_id == $0", _id).first().find() as TeacherAbsentAttendance
    }



    override suspend fun insertTeacherAbsent(teacherAbsentAttendance: TeacherAbsentAttendance) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(teacherAbsentAttendance.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateTeacherAbsent(teacherAbsentAttendance: TeacherAbsentAttendance) {
        realm.write {
            val queriedParent = query<TeacherAbsentAttendance>(query = "_id == $0", teacherAbsentAttendance._id).first().find()
            queriedParent?.reason = teacherAbsentAttendance.reason
        }
    }

    override suspend fun deleteTeacherAbsent(id: ObjectId) {
        realm.write {
            val teacherAbsentAttendance = query<TeacherAbsentAttendance>(query = "_id == $0", id).first().find()
            try {
                teacherAbsentAttendance?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteTeacherAbsentAll(term: List<TeacherAbsentAttendance>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    // Techer Profile
    override fun getTeacherData(): Flow<List<TeacherProfile>> {
        return realm.query<TeacherProfile>().asFlow().map { it.list }
    }

    override fun getTeacherWithID(_id: ObjectId): TeacherProfile {
        return realm.query<TeacherProfile>(query = "_id == $0", _id).first().find() as TeacherProfile
    }

    override fun getTeacherWithSurname(surname: String): Flow<List<TeacherProfile>> {
        return realm.query<TeacherProfile>(query = "surname CONTAINS[c] $0", surname).asFlow().map { it.list }
    }

    override suspend fun insertTeacher(teacherProfile: TeacherProfile) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(teacherProfile.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateTeacher(teacherProfile: TeacherProfile) {
        realm.write {
            val queriedParent = query<TeacherProfile>(query = "_id == $0", teacherProfile._id).first().find()
            queriedParent?.surname = teacherProfile.surname
            queriedParent?.otherNames = teacherProfile.otherNames
            queriedParent?.surname = teacherProfile.surname
            queriedParent?.position = teacherProfile.position
            queriedParent?.address = teacherProfile.address
            queriedParent?.phone = teacherProfile.phone
            queriedParent?.gender = teacherProfile.gender
            queriedParent?.dateOfBirth = teacherProfile.dateOfBirth
            queriedParent?.staffId = teacherProfile.staffId
            queriedParent?.relationshipStatus = teacherProfile.relationshipStatus
            queriedParent?.pics = teacherProfile.pics
        }
    }

    override suspend fun updateTeacherStatus(teacherProfile: TeacherProfile) {
        realm.write {
            val queriedParent = query<TeacherProfile>(query = "_id == $0", teacherProfile._id).first().find()
            queriedParent?.teacherStatus = teacherProfile.teacherStatus
        }

    }

    override suspend fun deleteTeacher(id: ObjectId) {
        realm.write {
            val access = query<TeacherProfile>(query = "_id == $0", id).first().find()
            try {
                access?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteTeacherProfileAll(term: List<TeacherProfile>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    // Teacher Attendance
    override fun getTeacherAttendanceData(): Flow<List<StaffAttendance>> {
        return realm.query<StaffAttendance>().asFlow().map { it.list }
    }

    override fun getStaffWithName(name: String): Flow<List<StaffAttendance>> {
        return realm.query<StaffAttendance>(query = "name CONTAINS[c] $0", name).asFlow().map { it.list }
    }

    override fun getTeacherAttendanceWithID(_id: ObjectId): StaffAttendance {
        return realm.query<StaffAttendance>(query = "_id == $0", _id).first().find() as StaffAttendance
    }

    override suspend fun insertTeacherAttendance(teacherAttendance: StaffAttendance) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(teacherAttendance.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.e("teacherAttendance", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateTeacherAttendance(teacherAttendance: StaffAttendance) {
        realm.write {
            val queriedParent = query<StaffAttendance>(query = "_id == $0", teacherAttendance._id).first().find()
//            queriedParent?.name = teacherAttendance.name
//            queriedParent?.staffId = teacherAttendance.staffId
//            queriedParent?.position = teacherAttendance.position
//            queriedParent?.timeIn = teacherAttendance.timeIn
            queriedParent?.timeOut = teacherAttendance.timeOut
//            queriedParent?.date = teacherAttendance.date
        }
    }

    override suspend fun deleteTeacherAttendance(id: ObjectId) {
        realm.write {
            val access = query<StaffAttendance>(query = "_id == $0", id).first().find()
            try {
                access?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteStaffAttendanceAll(term: List<StaffAttendance>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }


    // Access
    override fun getAccessData(): Flow<List<Access>> {
        return realm.query<Access>().asFlow().map { it.list }
    }

    override fun getAccessWithID(_id: ObjectId): Access {
        return realm.query<Access>(query = "_id == $0", _id).first().find() as Access

    }

    override suspend fun insertAccess(access: Access) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(access.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateAccess(access: Access) {
        realm.write {
            val queriedAccess = query<Access>(query = "_id == $0", access._id).first().find()
            queriedAccess?.admin = access.admin
            queriedAccess?.schoolOwner = access.schoolOwner
            queriedAccess?.attendanceCollected = access.attendanceCollected
        }
    }

    override suspend fun deleteAccess(id: ObjectId) {
        realm.write {
            val access = query<Access>(query = "_id == $0", id).first().find()
            try {
                access?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteAccessAll(term: List<Access>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }


    // Parent
    override fun getParentData(): Flow<List<Parent>> {
        return realm.query<Parent>().asFlow().map { it.list }
    }

    override fun getParentWithID(_id: ObjectId): Parent {
        return realm.query<Parent>(query = "_id == $0", _id).first().find() as Parent
    }

    override fun getParentWithSurname(surname: String): Flow<List<Parent>> {
        return realm.query<Parent>(query = "surname CONTAINS[c] $0", surname).asFlow().map { it.list }
    }

    override suspend fun insertParent(parent: Parent) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(parent.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateParent(parent: Parent) {
        realm.write {
            val queriedParent = query<Parent>(query = "_id == $0", parent._id).first().find()
            queriedParent?.surname = parent.surname
            queriedParent?.otherNames = parent.otherNames
            queriedParent?.phone = parent.phone
            queriedParent?.relationship = parent.relationship
            queriedParent?.gender = parent.gender
            queriedParent?.address = parent.address
            queriedParent?.pics = parent.pics
        }
    }

    override suspend fun deleteParent(id: ObjectId) {
        realm.write {
            val parent = query<Parent>(query = "_id == $0", id).first().find()
            try {
                parent?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteParentAll(term: List<Parent>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }


    // Student
    override fun getStudentData(): Flow<List<StudentData>> {
        return realm.query<StudentData>().asFlow().map { it.list }
    }

    override fun getStudentWithID(_id: ObjectId): StudentData {
        return realm.query<StudentData>(query = "_id == $0", _id).first().find() as StudentData
    }

    override fun getStudentWithClass(cless: String): Flow<List<StudentData>> {
        return realm.query<StudentData>(query = "cless CONTAINS[c] $0", cless).asFlow().map { it.list }

    }

    override fun getStudentWithChildId(studentId: String): StudentData {
        return realm.query<StudentData>(query = "studentApplicationID == $0", studentId).first().find() as StudentData

    }

    override fun getStudentWithFamilyID(familyId: String): Flow<List<StudentData>> {
        return realm.query<StudentData>(query = "familyId CONTAINS[c] $0", familyId).asFlow().map { it.list }
    }
    override fun getStudentWithSurname(surname: String): Flow<List<StudentData>> {
        return realm.query<StudentData>(query = "surname CONTAINS[c] $0", surname).asFlow().map { it.list }
    }


    override suspend fun insertStudent(student: StudentData) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(student.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateStudent(student: StudentData) {
        realm.write {
            val queriedStudent = query<StudentData>(query = "_id == $0", student._id).first().find()
            queriedStudent?.surname = student.surname
            queriedStudent?.otherNames = student.otherNames
            queriedStudent?.cless = student.cless
            queriedStudent?.arms = student.arms
            queriedStudent?.datOfBirth= student.datOfBirth
            queriedStudent?.gender= student.gender
            queriedStudent?.familyId= student.familyId
            queriedStudent?.studentApplicationID= student.studentApplicationID
            queriedStudent?.pics = student.pics
        }
    }

    override suspend fun updateStudentClass(student: StudentData) {
        realm.write {
            val queriedStudent = query<StudentData>(query = "_id == $0", student._id).first().find()
            queriedStudent?.cless = student.cless

        }
    }

    override suspend fun updateStudentStatus(student: StudentData) {
        realm.write {
            val queriedStudent = query<StudentData>(query = "_id == $0", student._id).first().find()
            queriedStudent?.studentStatus = student.studentStatus

        }
    }

    override suspend fun updateStudentFamilyId(student: StudentData) {
        realm.write {
            val queriedStudent = query<StudentData>(query = "_id == $0", student._id).first().find()
            queriedStudent?.familyId = student.familyId
        }
    }

    override suspend fun deleteStudent(id: ObjectId) {
        realm.write {
            val student = query<StudentData>(query = "_id == $0", id).first().find()
            try {
                student?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteStudentDataAll(term: List<StudentData>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }


    // Associate Parent
    override fun getAssociateParentData(): Flow<List<AssociateParent>> {
        return realm.query<AssociateParent>().asFlow().map { it.list }
    }

    override fun getAssociateParentWithID(_id: ObjectId): AssociateParent {
        return realm.query<AssociateParent>(query = "_id == $0", _id).first().find() as AssociateParent
    }

    override fun getAssociateParentWithFamilyId(familyId: String): Flow<List<AssociateParent>> {
        return realm.query<AssociateParent>(query = "familyId CONTAINS[c] $0", familyId).asFlow().map { it.list }

    }

    override suspend fun insertAssociateParent(associateParent: AssociateParent) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(associateParent.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateAssociateParent(associateParent: AssociateParent) {
        realm.write {
            val queriedParent = query<AssociateParent>(query = "_id == $0", associateParent._id).first().find()
            queriedParent?.surname = associateParent.surname
            queriedParent?.otherNames = associateParent.otherNames
            queriedParent?.phone = associateParent.phone
            queriedParent?.relationship = associateParent.relationship
            queriedParent?.address = associateParent.address
            queriedParent?.familyId = associateParent.familyId
            queriedParent?.pics = associateParent.pics
            queriedParent?.gender = associateParent.gender
        }
    }

    override suspend fun deleteAssociateParent(id: ObjectId) {
        realm.write {
            val associateParent = query<AssociateParent>(query = "_id == $0", id).first().find()
            try {
                associateParent?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteAssociateParentAll(term: List<AssociateParent>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }


    // Class Names

    override fun getClassNames(): Flow<List<ClassName>> {
        return realm.query<ClassName>().asFlow().map { it.list }
    }

    override fun getClassNamesWithID(_id: ObjectId): ClassName {
        return realm.query<ClassName>(query = "_id == $0", _id).first().find() as ClassName
    }

    override suspend fun insertClassNames(className: ClassName) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(className.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateClassNames(className: ClassName) {
        realm.write {
            val queriedParent = query<ClassName>(query = "_id == $0", className._id).first().find()
            queriedParent?.className = className.className
        }
    }

    override suspend fun deleteClassNames(id: ObjectId) {
        realm.write {
            val associateParent = query<ClassName>(query = "_id == $0", id).first().find()
            try {
                associateParent?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteClassNameAll(term: List<ClassName>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }


    // Class Arms
    override fun getClassArms(): Flow<List<ClassArms>> {
        return realm.query<ClassArms>().asFlow().map { it.list }
    }

    override fun getClassArmsWithID(_id: ObjectId): ClassArms {
        return realm.query<ClassArms>(query = "_id == $0", _id).first().find() as ClassArms
    }

    override suspend fun insertClassArms(classArms: ClassArms) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(classArms.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateClassArms(classArms: ClassArms) {
        realm.write {
            val queriedParent = query<ClassArms>(query = "_id == $0", classArms._id).first().find()
            queriedParent?.armsName = classArms.armsName
        }
    }

    override suspend fun deleteClassArms(id: ObjectId) {
        realm.write {
            val classArms = query<ClassArms>(query = "_id == $0", id).first().find()
            try {
                classArms?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteClassArmsAll(term: List<ClassArms>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    // Term
    override fun getTerm(): Flow<List<Term>> {
        return realm.query<Term>().asFlow().map { it.list }
    }

    override fun getTermWithId(_id: ObjectId): Term {
        return realm.query<Term>(query = "_id == $0", _id).first().find() as Term
    }


    override fun getTermWithtermname(termName: String): Term {
        return realm.query<Term>(query = "termName == $0", termName).first().find() as Term

    }

    override suspend fun insertTerm(term: Term) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(term.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateTerm(term: Term) {
        realm.write {
            val queriedParent = query<Term>(query = "_id == $0", term._id).first().find()
            queriedParent?.termStart = term.termStart
            queriedParent?.termEnd = term.termEnd
        }
    }

    override suspend fun deleteTerm(id: ObjectId) {
        realm.write {
            val classArms = query<Term>(query = "_id == $0", id).first().find()
            try {
                classArms?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    // Session
    override fun getSession(): Flow<List<Session>> {
        return realm.query<Session>().asFlow().map { it.list }
    }

    override fun getSessionWithID(_id: ObjectId): Session {
        return realm.query<Session>(query = "_id == $0", _id).first().find() as Session
    }

    override suspend fun insertSession(session: Session) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(session.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateSession(session: Session) {
        realm.write {
            val queriedParent = query<Session>(query = "_id == $0", session._id).first().find()
            queriedParent?.year = session.year

        }
    }


    override suspend fun deleteSession(id: ObjectId) {
        realm.write {
            val classArms = query<Term>(query = "_id == $0", id).first().find()
            try {
                classArms?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteYearAll(term: List<Session>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteSessionAll(term: List<Term>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }



    // Attendance
    override fun getAttendance(): Flow<List<AttendanceStudent>> {
        return realm.query<AttendanceStudent>().asFlow().map { it.list }
    }

    override fun getAttendanceWithID(_id: ObjectId): AttendanceStudent {
        return realm.query<AttendanceStudent>(query = "_id == $0", _id).first().find() as AttendanceStudent
    }

    override fun getAttendanceWithFamilyID(familyId: String): Flow<List<AttendanceStudent>> {
        return realm.query<AttendanceStudent>(query = "familyId CONTAINS[c] $0", familyId).asFlow().map { it.list }
    }

    override suspend fun insertAttendance(attendanceModel: AttendanceStudent) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(attendanceModel.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updateAttendance(attendanceModel: AttendanceStudent) {
        realm.write {
            val queriedParent = query<AttendanceStudent>(query = "_id == $0", attendanceModel._id).first().find()
//            queriedParent?.familyId = attendanceModel.familyId
//            queriedParent?.term = attendanceModel.term
//            queriedParent?.session = attendanceModel.session
//            queriedParent?.date = attendanceModel.date
//            queriedParent?.studentIn = attendanceModel.studentIn
            queriedParent?.studentOut = attendanceModel.studentOut
        }
    }


    override suspend fun deleteAttendance(date: String) {
        realm.write {
            val attendance = query<AttendanceStudent>(query = "date == $0", date).first().find()
            try {
                attendance?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("attendance", "${e.message}")
            }
        }
    }


    override suspend fun deleteAttendanceStudentAll(term: List<AttendanceStudent>) {
        realm.write {
            try {
                deleteAll().let { term }
            } catch (e: Exception) {
                Log.d("MongoRepositoryImpl", "${e.message}")
            }
        }
    }

}
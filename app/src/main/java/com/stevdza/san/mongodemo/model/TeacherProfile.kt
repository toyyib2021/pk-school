package com.stevdza.san.mongodemo.model

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class TeacherProfile : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var owner_id: String = ""
    var staffId: String = ""
    var surname: String = ""
    var otherNames: String = ""
    var position: String = ""
    var address: String = ""
    var phone: String = ""
    var gender: String = ""
    var dateOfBirth: String = ""
    var teacherStatus: StudentStatus? = null
    var pics:  String = ""
    var dateCreated:  String = ""
    var relationshipStatus:  String = ""

}

enum class StaffStatusType(val status: String){
    SACKED("Sacked"),
    ACTIVE("Active"),
    RESIGNED("Re signed")
}



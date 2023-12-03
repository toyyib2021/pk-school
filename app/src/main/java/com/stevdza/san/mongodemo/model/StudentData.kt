package com.stevdza.san.mongodemo.model

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class StudentData : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var owner_id: String = ""
    var surname: String = ""
    var otherNames: String = ""
    var cless: String = ""
    var arms: String = ""
    var datOfBirth: String = ""
    var dateCreated: String = ""
    var gender: String = ""
    var familyId: String = ""
    var studentApplicationID: String = ""
    var studentStatus: StudentStatus? = null
    var pics: String = ""
}





class StudentStatus: EmbeddedRealmObject {
    var status: String = ""
    var date: String = ""
}


enum class StudentStatusType(val status: String){
    SUSPENDED("Suspended"),
    ACTIVE("Active"),
    DROP_OUT("Drop Out")
}



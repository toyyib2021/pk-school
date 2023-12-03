package com.stevdza.san.mongodemo.model

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class AttendanceStudent : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var owner_id: String = ""
    var familyId: String = ""
    var session: String = ""
    var term: String = ""
    var date: String = ""
    var cless: String = ""
    var studentName: String = ""
    var studentId: String = ""
    var studentIn: StudentIn? = null
    var studentOut: StudentOut? = null
}


class StudentIn: EmbeddedRealmObject {
    var studentBroughtBy: String = ""
    var time: String = ""
}

class StudentOut: EmbeddedRealmObject {
    var studentBroughtBy: String = ""
    var time: String = ""
}




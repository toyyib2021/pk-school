package com.stevdza.san.mongodemo.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class StaffAttendance: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var owner_id: String = ""
    var staffId: String = ""
    var name: String = ""
    var position: String = ""
    var timeIn: String = ""
    var timeOut: String = ""
    var date: String = ""
    var month: String = ""
    var term: String = ""
    var session: String = ""
}
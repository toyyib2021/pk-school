package com.stevdza.san.mongodemo.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Parent : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var owner_id: String = ""
    var surname: String = ""
    var otherNames: String = ""
    var phone: String = ""
    var relationship: String = ""
    var address: String = ""
    var gender: String  = ""
    var pics: String = ""
}


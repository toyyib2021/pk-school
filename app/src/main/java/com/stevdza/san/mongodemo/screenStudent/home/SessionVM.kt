package com.stevdza.san.mongodemo.screenStudent.home

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.Session
import com.stevdza.san.mongodemo.model.Term
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class SessionVM (): ViewModel() {

    var terms = mutableStateOf(emptyList<Term>())
    var sessions = mutableStateOf(emptyList<Session>())
    var termName = mutableStateOf("")
    var termStart = mutableStateOf("")
    var termEnd = mutableStateOf("")
    var oneTerm = mutableStateOf<Term?>(null)
    var objectId = mutableStateOf("")

    var objectIdSessionYear = mutableStateOf("")
    var sessionYear = mutableStateOf("")





    init {
        getAllTerm()
        viewModelScope.launch {
            AlkhairDB.getSession().collect {
                sessions.value = it
            }
        }
    }

     fun getAllTerm() {
        viewModelScope.launch {
            AlkhairDB.getTerm().collect {
                terms.value = it
            }
        }
    }

    fun getTermWithTermName() {
        viewModelScope.launch {
            AlkhairDB.getTermWithtermname(termName = termName.value).let {
                oneTerm.value = it
            }
        }
    }









    fun updateTermDate(
        onSuccess:()->Unit,
        onError:()->Unit,
        emptyFeild:()->Unit,

        ) {
        viewModelScope.launch {
            if (objectId.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.updateTerm(term = Term().apply {
                            _id = BsonObjectId(hexString = this@SessionVM.objectId.value)
//                            termName = this@SessionVM.termName.value
                            termStart = this@SessionVM.termStart.value
                            termEnd = this@SessionVM.termEnd.value

                        })
                        return@withContext true
                    }
                    withContext(Dispatchers.Main) {
                        if (result) {
                            onSuccess()
                        }
                    }

                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "updateClassNames: ${e.message}",)
                    onError()
                }

            }else{
                emptyFeild()
            }
        }
    }

    fun updateSessionYear(
        onSuccess:()->Unit,
        onError:()->Unit,
        emptyFeild:()->Unit,

    ) {
        viewModelScope.launch {
            if (objectIdSessionYear.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.updateSession(session = com.stevdza.san.mongodemo.model.Session().apply {
                            _id = BsonObjectId(hexString = this@SessionVM.objectIdSessionYear.value)
                            year = this@SessionVM.sessionYear.value

                        })
                        return@withContext true
                    }
                    withContext(Dispatchers.Main) {
                        if (result) {
                            onSuccess()
                        }
                    }

                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "updateClassNames: ${e.message}",)
                    onError()
                }

            }else{
                emptyFeild()
            }
        }
    }


    fun deleteTerm(
        onSuccess:()->Unit,
        onError:()->Unit,
        emptyFeild:()->Unit,
    ) {
        viewModelScope.launch {
            if (objectId.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        viewModelScope.launch {
                            if (objectId.value.isNotEmpty()) {
                                AlkhairDB.deleteTerm(id = BsonObjectId(hexString = objectId.value))
                            }
                        }
                        return@withContext true
                    }
                    withContext(Dispatchers.Main) {
                        if (result) {
                            onSuccess()
                        }
                    }

                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "updateClassNames: ${e.message}",)
                    onError()
                }

            }else{
                emptyFeild()
            }
        }

    }


}
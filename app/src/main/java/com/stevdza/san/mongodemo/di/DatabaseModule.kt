package com.stevdza.san.mongodemo.di
//
//import com.stevdza.san.mongodemo.data.MongoRepository
//import com.stevdza.san.mongodemo.data.MongoRepositoryImpl
//import com.stevdza.san.mongodemo.model.Access
//import com.stevdza.san.mongodemo.model.AssociateParent
//import com.stevdza.san.mongodemo.model.AttendanceModel
//import com.stevdza.san.mongodemo.model.ClassArms
//import com.stevdza.san.mongodemo.model.ClassName
//import com.stevdza.san.mongodemo.model.Parent
//import com.stevdza.san.mongodemo.model.Session
//import com.stevdza.san.mongodemo.model.StudentData
//import com.stevdza.san.mongodemo.model.StudentInAndOut
//import com.stevdza.san.mongodemo.model.StudentStatus
//import com.stevdza.san.mongodemo.model.Term
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import io.realm.kotlin.Realm
//import io.realm.kotlin.RealmConfiguration
//import javax.inject.Singleton
//
//@InstallIn(SingletonComponent::class)
//@Module
//object DatabaseModule {
//
//    @Singleton
//    @Provides
//    fun provideRealm(): Realm {
//        val config = RealmConfiguration.Builder(
//            schema = setOf(
//                Access::class, AssociateParent::class,
//                Parent::class, ClassName::class, ClassArms::class, Term::class, Session::class,
//                AttendanceModel::class, StudentInAndOut::class, StudentData::class,
//                StudentStatus::class
//            )
//        )
//            .compactOnLaunch()
//            .build()
//        return Realm.open(config)
//    }
//
//    @Singleton
//    @Provides
//    fun provideMongoRepository(realm: Realm): MongoRepository {
//        return MongoRepositoryImpl(realm = realm)
//    }
//
//}
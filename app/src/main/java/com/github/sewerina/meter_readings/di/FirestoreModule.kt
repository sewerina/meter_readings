package com.github.sewerina.meter_readings.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class FirestoreModule {
    @Provides
    @Singleton
    fun firebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    @Named("readings")
    fun referenceReadings(firebaseFirestore: FirebaseFirestore): CollectionReference {
        return firebaseFirestore.collection("readings")
    }

    @Provides
    @Singleton
    @Named("homes")
    fun referenceHomes(firebaseFirestore: FirebaseFirestore): CollectionReference {
        return firebaseFirestore.collection("homes")
    }

    @Provides
    @Singleton
    @Named("reports")
    fun referenceReports(firebaseFirestore: FirebaseFirestore): CollectionReference {
        return firebaseFirestore.collection("reports")
    }
}
package com.github.sewerina.meter_readings.ui;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirestoreModule {

    @Provides
    @Singleton
    public FirebaseFirestore firebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    @Named("readings")
    public CollectionReference referenceReadings(FirebaseFirestore firebaseFirestore) {
        return firebaseFirestore.collection("readings");
    }

    @Provides
    @Singleton
    @Named("homes")
    public CollectionReference referenceHomes(FirebaseFirestore firebaseFirestore) {
        return firebaseFirestore.collection("homes");
    }

}

package com.example.ecotrack.data

import com.example.ecotrack.data.model.EcoActivity
import com.example.ecotrack.data.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Utility class for managing Firebase Realtime Database operations
 */
object FirebaseManager {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")
    private val activitiesRef: DatabaseReference = database.getReference("activities")

    /**
     * Creates or updates a user profile in the database
     */
    suspend fun saveUser(user: User): Boolean = suspendCoroutine { continuation ->
        usersRef.child(user.uid).setValue(user)
            .addOnSuccessListener { continuation.resume(true) }
            .addOnFailureListener { continuation.resumeWithException(it) }
    }

    /**
     * Retrieves a user profile from the database
     */
    suspend fun getUser(uid: String): User? = suspendCoroutine { continuation ->
        usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                continuation.resume(user)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    /**
     * Records a new environmental activity
     */
    suspend fun saveActivity(activity: EcoActivity): Boolean = suspendCoroutine { continuation ->
        val activityId = activitiesRef.push().key ?: return@suspendCoroutine continuation.resume(false)
        val updatedActivity = activity.copy(id = activityId)
        
        activitiesRef.child(activityId).setValue(updatedActivity)
            .addOnSuccessListener { continuation.resume(true) }
            .addOnFailureListener { continuation.resumeWithException(it) }
    }

    /**
     * Retrieves all activities for a specific user
     */
    suspend fun getUserActivities(userId: String): List<EcoActivity> = suspendCoroutine { continuation ->
        activitiesRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val activities = mutableListOf<EcoActivity>()
                    snapshot.children.forEach { child ->
                        child.getValue(EcoActivity::class.java)?.let {
                            activities.add(it)
                        }
                    }
                    continuation.resume(activities)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
    }

    /**
     * Updates the eco points for a user
     */
    suspend fun updateUserEcoPoints(userId: String, points: Int): Boolean = suspendCoroutine { continuation ->
        usersRef.child(userId).child("totalEcoPoints")
            .setValue(points)
            .addOnSuccessListener { continuation.resume(true) }
            .addOnFailureListener { continuation.resumeWithException(it) }
    }
}
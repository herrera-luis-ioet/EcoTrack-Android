package com.example.ecotrack.data

import com.example.ecotrack.data.model.User
import com.example.ecotrack.data.model.EcoActivity
import com.google.firebase.database.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseManagerTest {
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var activitiesRef: DatabaseReference

    @Before
    fun setup() {
        mockkStatic(FirebaseDatabase::class)
        database = mockk(relaxed = true)
        usersRef = mockk(relaxed = true)
        activitiesRef = mockk(relaxed = true)

        every { FirebaseDatabase.getInstance() } returns database
        every { database.getReference("users") } returns usersRef
        every { database.getReference("activities") } returns activitiesRef
    }

    @Test
    fun `test save user success`() = runTest {
        // Prepare test data
        val testUser = User(
            uid = "test123",
            email = "test@example.com",
            displayName = "Test User",
            totalEcoPoints = 0
        )

        // Mock database operation
        val task: com.google.android.gms.tasks.Task<Void> = mockk(relaxed = true)
        every { task.isSuccessful } returns true
        every { usersRef.child(testUser.uid).setValue(testUser) } returns task

        // Execute test
        val result = FirebaseManager.saveUser(testUser)

        // Verify
        assertThat(result).isTrue()
        verify { usersRef.child(testUser.uid).setValue(testUser) }
    }

    @Test
    fun `test get user success`() = runTest {
        // Prepare test data
        val userId = "test123"
        val testUser = User(
            uid = userId,
            email = "test@example.com",
            displayName = "Test User",
            totalEcoPoints = 0
        )

        // Mock database snapshot
        val snapshot: DataSnapshot = mockk()
        every { snapshot.getValue(User::class.java) } returns testUser

        // Mock database operation
        val valueEventListener = slot<ValueEventListener>()
        every { 
            usersRef.child(userId).addListenerForSingleValueEvent(capture(valueEventListener))
        } answers {
            valueEventListener.captured.onDataChange(snapshot)
        }

        // Execute test
        val result = FirebaseManager.getUser(userId)

        // Verify
        assertThat(result).isEqualTo(testUser)
        verify { usersRef.child(userId).addListenerForSingleValueEvent(any()) }
    }

    @Test
    fun `test save activity success`() = runTest {
        // Prepare test data
        val testActivity = EcoActivity(
            id = "",
            userId = "test123",
            type = "RECYCLE",
            description = "Recycled paper",
            points = 10,
            timestamp = System.currentTimeMillis()
        )

        // Mock database operation
        val newKey = "activity123"
        every { activitiesRef.push().key } returns newKey
        
        val task: com.google.android.gms.tasks.Task<Void> = mockk(relaxed = true)
        every { task.isSuccessful } returns true
        every { activitiesRef.child(newKey).setValue(any()) } returns task

        // Execute test
        val result = FirebaseManager.saveActivity(testActivity)

        // Verify
        assertThat(result).isTrue()
        verify { 
            activitiesRef.child(newKey).setValue(match { 
                it is EcoActivity && it.id == newKey 
            })
        }
    }

    @Test
    fun `test get user activities success`() = runTest {
        // Prepare test data
        val userId = "test123"
        val testActivities = listOf(
            EcoActivity(
                id = "activity1",
                userId = userId,
                type = "RECYCLE",
                description = "Recycled paper",
                points = 10,
                timestamp = System.currentTimeMillis()
            ),
            EcoActivity(
                id = "activity2",
                userId = userId,
                type = "TRANSPORT",
                description = "Used bicycle",
                points = 20,
                timestamp = System.currentTimeMillis()
            )
        )

        // Mock database snapshot
        val childSnapshots = testActivities.map { activity ->
            mockk<DataSnapshot>().apply {
                every { getValue(EcoActivity::class.java) } returns activity
            }
        }
        val snapshot: DataSnapshot = mockk()
        every { snapshot.children } returns childSnapshots

        // Mock database query
        val query: Query = mockk()
        every { activitiesRef.orderByChild("userId").equalTo(userId) } returns query

        // Mock database operation
        val valueEventListener = slot<ValueEventListener>()
        every { 
            query.addListenerForSingleValueEvent(capture(valueEventListener))
        } answers {
            valueEventListener.captured.onDataChange(snapshot)
        }

        // Execute test
        val result = FirebaseManager.getUserActivities(userId)

        // Verify
        assertThat(result).containsExactlyElementsIn(testActivities)
        verify { 
            activitiesRef.orderByChild("userId").equalTo(userId)
            query.addListenerForSingleValueEvent(any())
        }
    }

    @Test
    fun `test update user eco points success`() = runTest {
        // Prepare test data
        val userId = "test123"
        val points = 100

        // Mock database operation
        val task: com.google.android.gms.tasks.Task<Void> = mockk(relaxed = true)
        every { task.isSuccessful } returns true
        every { 
            usersRef.child(userId).child("totalEcoPoints").setValue(points)
        } returns task

        // Execute test
        val result = FirebaseManager.updateUserEcoPoints(userId, points)

        // Verify
        assertThat(result).isTrue()
        verify { usersRef.child(userId).child("totalEcoPoints").setValue(points) }
    }

    @Test
    fun `test save user failure`() = runTest {
        // Prepare test data
        val testUser = User(
            uid = "test123",
            email = "test@example.com",
            displayName = "Test User",
            totalEcoPoints = 0
        )

        // Mock database operation failure
        val exception = DatabaseException("Network error")
        val task: com.google.android.gms.tasks.Task<Void> = mockk(relaxed = true)
        every { task.isSuccessful } returns false
        every { task.exception } returns exception
        every { usersRef.child(testUser.uid).setValue(testUser) } returns task

        try {
            FirebaseManager.saveUser(testUser)
            throw AssertionError("Expected exception was not thrown")
        } catch (e: DatabaseException) {
            assertThat(e.message).isEqualTo("Network error")
        }
    }

    @Test
    fun `test get user failure`() = runTest {
        // Prepare test data
        val userId = "test123"
        val error = DatabaseError.fromException(DatabaseException("Network error"))

        // Mock database operation failure
        val valueEventListener = slot<ValueEventListener>()
        every { 
            usersRef.child(userId).addListenerForSingleValueEvent(capture(valueEventListener))
        } answers {
            valueEventListener.captured.onCancelled(error)
        }

        try {
            FirebaseManager.getUser(userId)
            throw AssertionError("Expected exception was not thrown")
        } catch (e: DatabaseException) {
            assertThat(e.message).isEqualTo("Network error")
        }
    }
}
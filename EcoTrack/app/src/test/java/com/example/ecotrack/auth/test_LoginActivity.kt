package com.example.ecotrack.auth

import android.content.Intent
import android.view.View
import android.widget.Toast
import com.example.ecotrack.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.google.common.truth.Truth.assertThat

@RunWith(RobolectricTestRunner::class)
class LoginActivityTest {
    private lateinit var loginActivity: LoginActivity

    @MockK
    private lateinit var auth: FirebaseAuth

    @MockK
    private lateinit var emailEditText: TextInputEditText

    @MockK
    private lateinit var passwordEditText: TextInputEditText

    @MockK
    private lateinit var loginButton: MaterialButton

    @MockK
    private lateinit var registerButton: MaterialButton

    @MockK
    private lateinit var progressBar: View

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        loginActivity = spyk(LoginActivity())

        // Mock view finding
        every { loginActivity.findViewById<TextInputEditText>(any()) } returns emailEditText
        every { loginActivity.findViewById<MaterialButton>(any()) } returns loginButton
        every { loginActivity.findViewById<View>(any()) } returns progressBar

        // Mock FirebaseAuth instance
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns auth

        // Mock EditText behavior
        every { emailEditText.text.toString() } returns "test@example.com"
        every { passwordEditText.text.toString() } returns "password123"
        every { emailEditText.error = any() } just Runs
        every { passwordEditText.error = any() } just Runs
    }

    @Test
    fun `test successful login`() {
        // Mock successful authentication
        val authTask: Task<*> = mockk(relaxed = true)
        every { authTask.isSuccessful } returns true
        every { 
            auth.signInWithEmailAndPassword(any(), any())
        } returns authTask as Task<Void>

        // Mock navigation
        every { loginActivity.startActivity(any()) } just Runs
        every { loginActivity.finish() } just Runs

        // Trigger login
        loginActivity.loginUser()

        // Verify authentication was attempted
        verify { 
            auth.signInWithEmailAndPassword("test@example.com", "password123")
        }

        // Verify navigation occurred
        verify { 
            loginActivity.startActivity(any<Intent>())
            loginActivity.finish()
        }
    }

    @Test
    fun `test failed login`() {
        // Mock failed authentication
        val exception = Exception("Invalid credentials")
        val authTask: Task<*> = mockk(relaxed = true)
        every { authTask.isSuccessful } returns false
        every { authTask.exception } returns exception
        every { 
            auth.signInWithEmailAndPassword(any(), any())
        } returns authTask as Task<Void>

        // Mock toast showing
        mockkStatic(Toast::class)
        every { Toast.makeText(any(), any(), any()).show() } just Runs

        // Trigger login
        loginActivity.loginUser()

        // Verify error was shown
        verify { 
            Toast.makeText(loginActivity, "Authentication failed: Invalid credentials", any())
        }
    }

    @Test
    fun `test empty email validation`() {
        every { emailEditText.text.toString() } returns ""

        loginActivity.loginUser()

        verify { emailEditText.error = "Email is required" }
        verify(exactly = 0) { auth.signInWithEmailAndPassword(any(), any()) }
    }

    @Test
    fun `test empty password validation`() {
        every { passwordEditText.text.toString() } returns ""

        loginActivity.loginUser()

        verify { passwordEditText.error = "Password is required" }
        verify(exactly = 0) { auth.signInWithEmailAndPassword(any(), any()) }
    }

    @Test
    fun `test short password validation`() {
        every { passwordEditText.text.toString() } returns "12345"

        loginActivity.loginUser()

        verify { passwordEditText.error = "Password must be at least 6 characters" }
        verify(exactly = 0) { auth.signInWithEmailAndPassword(any(), any()) }
    }

    @Test
    fun `test successful registration`() {
        // Mock successful registration
        val authTask: Task<*> = mockk(relaxed = true)
        every { authTask.isSuccessful } returns true
        every { 
            auth.createUserWithEmailAndPassword(any(), any())
        } returns authTask as Task<Void>

        // Mock navigation
        every { loginActivity.startActivity(any()) } just Runs
        every { loginActivity.finish() } just Runs

        // Trigger registration
        loginActivity.registerUser()

        // Verify registration was attempted
        verify { 
            auth.createUserWithEmailAndPassword("test@example.com", "password123")
        }

        // Verify navigation occurred
        verify { 
            loginActivity.startActivity(any<Intent>())
            loginActivity.finish()
        }
    }

    @Test
    fun `test failed registration`() {
        // Mock failed registration
        val exception = Exception("Email already exists")
        val authTask: Task<*> = mockk(relaxed = true)
        every { authTask.isSuccessful } returns false
        every { authTask.exception } returns exception
        every { 
            auth.createUserWithEmailAndPassword(any(), any())
        } returns authTask as Task<Void>

        // Mock toast showing
        mockkStatic(Toast::class)
        every { Toast.makeText(any(), any(), any()).show() } just Runs

        // Trigger registration
        loginActivity.registerUser()

        // Verify error was shown
        verify { 
            Toast.makeText(loginActivity, "Registration failed: Email already exists", any())
        }
    }
}
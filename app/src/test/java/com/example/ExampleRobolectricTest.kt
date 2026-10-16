package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AmaAppRepository
import com.example.data.VerificationResult
import com.example.model.AppPlatform
import com.example.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AmaApp", appName)
    }

    @Test
    fun `verify user role permissions matrix`() {
        // Developer can upload, view analytics, manage versions, but cannot moderate forum
        assertTrue(UserRole.DEVELOPER.canUploadApps)
        assertTrue(UserRole.DEVELOPER.canViewAnalytics)
        assertTrue(UserRole.DEVELOPER.canManageVersions)
        assertFalse(UserRole.DEVELOPER.canModerateForum)
        assertFalse(UserRole.DEVELOPER.canApproveApps)

        // Tester can download and submit feedback, but cannot upload apps
        assertFalse(UserRole.TESTER.canUploadApps)
        assertFalse(UserRole.TESTER.canManageVersions)
        assertTrue(UserRole.TESTER.canSubmitFeedback)

        // Admin has overarching control
        assertTrue(UserRole.ADMIN.canUploadApps)
        assertTrue(UserRole.ADMIN.canModerateForum)
        assertTrue(UserRole.ADMIN.canApproveApps)
        assertTrue(UserRole.ADMIN.canManageUsers)
    }

    @Test
    fun `test blockchain IP registration and anti-clone priority verification`() {
        val repository = AmaAppRepository()

        // Verify initial seeded app
        val result = repository.verifyAppIntellectualProperty("com.novalabs.lumina.editor")
        assertTrue(result is VerificationResult.VerifiedOriginal)

        // Verify un-registered app detection
        val unknownResult = repository.verifyAppIntellectualProperty("com.random.unregistered.app")
        assertTrue(unknownResult is VerificationResult.NotRegistered)

        // Register new app and verify blockchain proof generation
        val newApp = repository.registerNewApp(
            title = "Quantum Cipher",
            packageName = "com.quantum.cipher.safe",
            shortPitch = "Lattice cryptography key exchange",
            description = "Post-quantum mobile cipher",
            category = "Security & Tools",
            platform = AppPlatform.ANDROID_APK,
            initialVersionName = "1.0.0",
            binarySizeMb = 18.4,
            releaseNotes = "Genesis test release"
        )

        val newAppResult = repository.verifyAppIntellectualProperty("com.quantum.cipher.safe")
        assertTrue(newAppResult is VerificationResult.VerifiedOriginal)
    }
}

package ga.lupuss.anotherbikeapp.models.firebase

import android.app.Activity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import org.junit.Test
import org.mockito.Mockito

class FirebaseAuthInteractorTest {

    private fun taskOnFailTriggers(exception: Exception): Task<AuthResult> {

         return mock {

            on { addOnFailureListener(any<Activity>(), any()) }.then {

                (it.getArgument(1) as OnFailureListener)
                        .onFailure(exception)

                mock<Task<AuthResult>> {}
            }
        }
    }

    @Test
    fun login_shouldInvokeSuccessCallbackOnSuccessLogin() {

        val firebaseAuth = mock<FirebaseAuth> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on {
                signInWithEmailAndPassword(any(), any())
                        .addOnSuccessListener(any<Activity>(), any())
            }.then {
                (it.getArgument(1) as OnSuccessListener<AuthResult?>).onSuccess(mock{})
                    mock<Task<AuthResult>> {}
            }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth, mock { }, mock { })

        val mockListener = mock<AuthInteractor.OnLoginDoneListener> { }

        firebaseAuthInteractor.login(
                "any",
                "any",
                mockListener,
                mock<Activity> {  })

        verify(mockListener, times(1)).onSuccess()
        verify(mockListener, never()).onUserNotExists()
        verify(mockListener, never()).onUndefinedError()
        verify(mockListener, never()).onInvalidCredentialsError()
    }

    @Test
    fun login_shouldInvokeInvalidCredentialsCallbackOnInvalidCredentials() {

        val taskMock: Task<AuthResult> =  taskOnFailTriggers(mock<FirebaseAuthInvalidCredentialsException> {})

        val firebaseAuth = mock<FirebaseAuth> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on { signInWithEmailAndPassword(any(), any()).addOnSuccessListener(any<Activity>(), any()) }
                    .then { taskMock }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth, mock { }, mock { })

        val mockListener = mock<AuthInteractor.OnLoginDoneListener> { }

        firebaseAuthInteractor.login(
                "any",
                "any",
                mockListener,
                mock<Activity> {  })

        verify(mockListener, never()).onSuccess()
        verify(mockListener, never()).onUserNotExists()
        verify(mockListener, never()).onUndefinedError()
        verify(mockListener, times(1)).onInvalidCredentialsError()
    }

    @Test
    fun login_shouldInvokeUndefinedErrorCallbackOnUndefinedError() {

        val taskMock = taskOnFailTriggers(mock {})

        val firebaseAuth = mock<FirebaseAuth> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on { signInWithEmailAndPassword(any(), any()).addOnSuccessListener(any<Activity>(), any()) }.then {
                taskMock
            }
        }


        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth, mock { }, mock { })

        val mockListener = mock<AuthInteractor.OnLoginDoneListener> { }

        firebaseAuthInteractor.login(
                "any",
                "any",
                mockListener,
                mock<Activity> {  })

        verify(mockListener, never()).onSuccess()
        verify(mockListener, never()).onUserNotExists()
        verify(mockListener, times(1)).onUndefinedError()
        verify(mockListener, never()).onInvalidCredentialsError()
    }

    @Test
    fun login_whenUserNotExist_shouldInvokeUserNotExistsCallback() {

        val taskMock =  taskOnFailTriggers(mock<FirebaseAuthInvalidUserException> {})

        val firebaseAuth = mock<FirebaseAuth> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on { signInWithEmailAndPassword(any(), any())
                    .addOnSuccessListener(any<Activity>(), any()) }.then { taskMock }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth, mock { }, mock { })

        val mockListener = mock<AuthInteractor.OnLoginDoneListener> { }

        firebaseAuthInteractor.login(
                "any",
                "any",
                mockListener,
                mock<Activity> {  })

        verify(mockListener, never()).onSuccess()
        verify(mockListener, times(1)).onUserNotExists()
        verify(mockListener, never()).onUndefinedError()
        verify(mockListener, never()).onInvalidCredentialsError()
    }

    @Test
    fun createAccount_shouldInvokeSuccessCallbackOnSuccessCreation() {

        val userMock = mock<FirebaseUser> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on {
                updateProfile(anyOrNull())
                        .continueWithTask(any<Continuation<Void, Task<Void>>>())
                        .addOnSuccessListener(any<Activity>(), any())
            }.then { mock<Task<Void>>{ } }
        }.also { clearInvocations(it) }

        val firebaseAuth = mock<FirebaseAuth> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on { createUserWithEmailAndPassword(any(), any())
                    .addOnSuccessListener(any<Activity>(), any())
            }.then {
                (it.getArgument(1) as OnSuccessListener<AuthResult?>).onSuccess(mock{})
                mock<Task<AuthResult>> {}
            }
            on { currentUser }.then { userMock }
        }


        val mockBuilder = mock<UserProfileChangeRequest.Builder> {
            on { setDisplayName(any()) }
                    .then { mock<UserProfileChangeRequest.Builder> { } }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(
                firebaseAuth,
                mockBuilder,
                mock { }
        )

        val mockListener = mock<AuthInteractor.OnAccountCreationDoneListener> { }

        firebaseAuthInteractor.createAccount(
                "correct@email.com",
                "moreThanSixChars",
                "any",
                mockListener,
                mock<Activity> {  })

        verify(mockListener, times(1)).onSuccess()
        verify(mockListener, never()).onTooWeakPassword()
        verify(mockListener, never()).onUndefinedError()
        verify(mockListener, never()).onInvalidCredentialsError()
        verify(mockListener, never()).onUserExist()
        verify(userMock, times(1)).updateProfile(anyOrNull())
        verify(mockBuilder, times(1)).setDisplayName("any")
    }

    @Test
    fun createAccount_shouldInvokeInvalidCredentialsCallbackOnInvalidCredentials() {

        val taskMock =  taskOnFailTriggers(mock<FirebaseAuthInvalidCredentialsException>{})

        val firebaseAuth = mock<FirebaseAuth> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on {
                createUserWithEmailAndPassword(any(), any())
                        .addOnSuccessListener(any<Activity>(), any())
            }.then { taskMock }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth, mock { }, mock { })

        val mockListener = mock<AuthInteractor.OnAccountCreationDoneListener> { }

        firebaseAuthInteractor.createAccount(
                "any",
                "moreThanSixChars",
                "any",
                mockListener,
                mock<Activity> {  })

        verify(mockListener, never()).onSuccess()
        verify(mockListener, never()).onUserExist()
        verify(mockListener, never()).onTooWeakPassword()
        verify(mockListener, never()).onUndefinedError()
        verify(mockListener, times(1)).onInvalidCredentialsError()
    }

    @Test
    fun createAccount_shouldInvokeUndefinedErrorCallbackOnUndefinedError() {

        val firebaseAuth = mock<FirebaseAuth> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on {
                createUserWithEmailAndPassword(any(), any())
                        .addOnSuccessListener(any<Activity>(), any())
            }.then { taskOnFailTriggers(mock {}) }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth, mock { }, mock { })

        val mockListener = mock<AuthInteractor.OnAccountCreationDoneListener> { }

        firebaseAuthInteractor.createAccount(
                "any",
                "moreThanSixChars",
                "any",
                mockListener,
                mock<Activity> {  })

        verify(mockListener, never()).onSuccess()
        verify(mockListener, never()).onUserExist()
        verify(mockListener, never()).onTooWeakPassword()
        verify(mockListener, times(1)).onUndefinedError()
        verify(mockListener, never()).onInvalidCredentialsError()
    }

    @Test
    fun createAccount_shouldInvokeUserExistCallbackOnExistingUser() {

        val firebaseAuth = mock<FirebaseAuth> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on {
                createUserWithEmailAndPassword(any(), any())
                        .addOnSuccessListener(any<Activity>(), any())
            }.then { taskOnFailTriggers(mock<FirebaseAuthUserCollisionException> {}) }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth, mock { }, mock { })

        val mockListener = mock<AuthInteractor.OnAccountCreationDoneListener> { }

        firebaseAuthInteractor.createAccount(
                "correct@email.com",
                "moreThanSixChars",
                "any",
                mockListener,
                mock<Activity> {  })

        verify(mockListener, never()).onSuccess()
        verify(mockListener, times(1)).onUserExist()
        verify(mockListener, never()).onTooWeakPassword()
        verify(mockListener, never()).onUndefinedError()
        verify(mockListener, never()).onInvalidCredentialsError()
    }

    @Test
    fun createAccount_shouldInvokeTooWeakPasswordCallbackOnWeakPassword() {

        val firebaseAuth = mock<FirebaseAuth> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on {
                createUserWithEmailAndPassword(any(), any())
                        .addOnSuccessListener(any<Activity>(), any())

            }.then { taskOnFailTriggers(mock<FirebaseAuthWeakPasswordException> {}) }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth, mock { }, mock { })

        val mockListener = mock<AuthInteractor.OnAccountCreationDoneListener> { }

        firebaseAuthInteractor.createAccount(
                "correct@email.com",
                "any",
                "any",
                mockListener,
                mock<Activity> {  })

        verify(mockListener, never()).onSuccess()
        verify(mockListener, never()).onUserExist()
        verify(mockListener, times(1)).onTooWeakPassword()
        verify(mockListener, never()).onUndefinedError()
        verify(mockListener, never()).onInvalidCredentialsError()
    }

    @Test
    fun setDisplayName_shouldInvokeSuccessCallbackOnSuccessChange() {

        val userMock = mock<FirebaseUser> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on {
                updateProfile(anyOrNull())
                        .continueWithTask(any<Continuation<Void, Task<Void>>>())
                        .addOnSuccessListener(any<Activity>(), any())
            }.then {
                (it.getArgument(1) as OnSuccessListener<Void>)
                        .onSuccess(mock {})
                mock<Task<Void>>{ }
            }
        }.also { clearInvocations(it) }

        val firebaseAuth = mock<FirebaseAuth> { on { currentUser }.then { userMock } }


        val mockBuilder = mock<UserProfileChangeRequest.Builder> {
            on { setDisplayName(any()) }
                    .then { mock<UserProfileChangeRequest.Builder> { } }
        }

        val mockListener = mock<AuthInteractor.OnDisplayNameSetDoneListener> {}

        FirebaseAuthInteractor(firebaseAuth, mockBuilder, mock { }).setDisplayName(
                "name",
                mockListener,
                mock<Activity> {}
        )

        verify(mockListener, times(1)).onSuccessSettingDisplayName()
        verify(mockBuilder, times(1)).setDisplayName("name")
        verify(userMock, times(1)).updateProfile(anyOrNull())

    }

    @Test
    fun setDisplayName_shouldInvokeOnFailCallbackOnFailedChange() {

        val userMock = mock<FirebaseUser> (defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on {
                updateProfile(anyOrNull())
                        .continueWithTask(any<Continuation<Void, Task<Void>>>())
                        .addOnSuccessListener(any<Activity>(), any())
                        .addOnFailureListener(any<Activity>(), any())
            }.then {
                (it.getArgument(1) as OnFailureListener).onFailure(mock{})
                mock<Task<Void>>{}
            }
        }.also { clearInvocations(it) }

        val firebaseAuth = mock<FirebaseAuth> { on { currentUser }.then { userMock } }

        val mockBuilder = mock<UserProfileChangeRequest.Builder> {
            on { setDisplayName(any()) }
                    .then { mock<UserProfileChangeRequest.Builder> { } }
        }

        val mockListener = mock<AuthInteractor.OnDisplayNameSetDoneListener> {}

        FirebaseAuthInteractor(firebaseAuth, mockBuilder, mock { }).setDisplayName(
                "name",
                mockListener,
                mock<Activity> {}
        )

        verify(mockListener, never()).onSuccessSettingDisplayName()
        verify(mockListener, times(1)).onFailSettingDisplayName()
        verify(mockBuilder, times(1)).setDisplayName("name")
        verify(userMock, times(1)).updateProfile(anyOrNull())
    }

    @Test
    fun signOut_shouldDelegateToFirebaseAuth() {

        val firebaseAuth = mock<FirebaseAuth> {  }

        FirebaseAuthInteractor(firebaseAuth, mock { }, mock { }).signOut()

        verify(firebaseAuth, times(1)).signOut()
    }

}
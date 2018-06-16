package ga.lupuss.anotherbikeapp.models.firebase

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import org.junit.Test

class FirebaseAuthInteractorTest {


    // That whole mocking stuff for firebase just triggers proper callbacks

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

        val firebaseAuth = mock<FirebaseAuth> {
            on { signInWithEmailAndPassword(any(), any()) }.then {
                mock<Task<AuthResult>> {
                    on { addOnSuccessListener(any<Activity>(), any()) }
                            .then {
                                (it.getArgument(1) as OnSuccessListener<AuthResult?>).onSuccess(mock{})
                                mock<Task<AuthResult>> {}
                            }
                }
            }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth)

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

        val firebaseAuth = mock<FirebaseAuth> {
            on { signInWithEmailAndPassword(any(), any()) }.then {

                mock<Task<AuthResult>> {

                    on { addOnSuccessListener(any<Activity>(), any()) }.then { taskMock }
                }
            }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth)

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

        val firebaseAuth = mock<FirebaseAuth> {
            on { signInWithEmailAndPassword(any(), any()) }.then {

                mock<Task<AuthResult>> {

                    on { addOnSuccessListener(any<Activity>(), any()) }.then { taskMock }
                }
            }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth)

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
    fun login_shouldInvokeUserNotExistsCallbackOnNotExistingUser() {

        val taskMock =  taskOnFailTriggers(mock<FirebaseAuthInvalidUserException> {})

        val firebaseAuth = mock<FirebaseAuth> {
            on { signInWithEmailAndPassword(any(), any()) }.then {

                mock<Task<AuthResult>> {

                    on { addOnSuccessListener(any<Activity>(), any()) }.then { taskMock }
                }
            }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth)

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

        val userMock = mock<FirebaseUser> {
            on { updateProfile(anyOrNull()) }.then {
                mock<Task<Void>> {
                    on { addOnSuccessListener(any<Activity>(), any()) }.then { mock<Task<Void>>{ } }
                }
            }
        }

        val firebaseAuth = mock<FirebaseAuth> {
            on { createUserWithEmailAndPassword(any(), any()) }.then {
                mock<Task<AuthResult>> {
                    on { addOnSuccessListener(any<Activity>(), any()) }
                            .then {
                                (it.getArgument(1) as OnSuccessListener<AuthResult?>).onSuccess(mock{})
                                mock<Task<AuthResult>> {}
                            }
                }
            }

            on { currentUser }.then { userMock }
        }


        val mockBuilder = mock<UserProfileChangeRequest.Builder> {
            on { setDisplayName(any()) }
                    .then { mock<UserProfileChangeRequest.Builder> { } }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(
                firebaseAuth,
                mockBuilder
        )

        val mockListener = mock<AuthInteractor.OnAccountCreateDoneListener> { }

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

        val firebaseAuth = mock<FirebaseAuth> {
            on { createUserWithEmailAndPassword(any(), any()) }.then {

                mock<Task<AuthResult>> {

                    on { addOnSuccessListener(any<Activity>(), any()) }.then { taskMock }
                }
            }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth)

        val mockListener = mock<AuthInteractor.OnAccountCreateDoneListener> { }

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

        val taskMock =  taskOnFailTriggers(mock {})

        val firebaseAuth = mock<FirebaseAuth> {
            on { createUserWithEmailAndPassword(any(), any()) }.then {

                mock<Task<AuthResult>> {

                    on { addOnSuccessListener(any<Activity>(), any()) }.then { taskMock }
                }
            }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth)

        val mockListener = mock<AuthInteractor.OnAccountCreateDoneListener> { }

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

        val taskMock =  taskOnFailTriggers(mock<FirebaseAuthUserCollisionException> {})

        val firebaseAuth = mock<FirebaseAuth> {
            on { createUserWithEmailAndPassword(any(), any()) }.then {

                mock<Task<AuthResult>> {

                    on { addOnSuccessListener(any<Activity>(), any()) }.then { taskMock }
                }
            }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth)

        val mockListener = mock<AuthInteractor.OnAccountCreateDoneListener> { }

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

        val taskMock =  taskOnFailTriggers(mock<FirebaseAuthWeakPasswordException> {})

        val firebaseAuth = mock<FirebaseAuth> {
            on { createUserWithEmailAndPassword(any(), any()) }.then {

                mock<Task<AuthResult>> {

                    on { addOnSuccessListener(any<Activity>(), any()) }.then { taskMock }
                }
            }
        }

        val firebaseAuthInteractor = FirebaseAuthInteractor(firebaseAuth)

        val mockListener = mock<AuthInteractor.OnAccountCreateDoneListener> { }

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

        val userMock = mock<FirebaseUser> {
            on { updateProfile(anyOrNull()) }.then {
                mock<Task<Void>> {

                    on { addOnSuccessListener(any<Activity>(), any()) }.then {
                        (it.getArgument(1) as OnSuccessListener<Void>).onSuccess(mock {})
                        mock<Task<Void>>{ }
                    }
                }
            }
        }

        val firebaseAuth = mock<FirebaseAuth> { on { currentUser }.then { userMock } }


        val mockBuilder = mock<UserProfileChangeRequest.Builder> {
            on { setDisplayName(any()) }
                    .then { mock<UserProfileChangeRequest.Builder> { } }
        }

        val mockListener = mock<AuthInteractor.OnDisplayNameSetDoneListener> {}

        FirebaseAuthInteractor(firebaseAuth, mockBuilder).setDisplayName(
                "name",
                mockListener,
                mock<Activity> {}
        )

        verify(mockListener, times(1)).onSuccessNameChange()
        verify(mockBuilder, times(1)).setDisplayName("name")
        verify(userMock, times(1)).updateProfile(anyOrNull())
    }

    @Test
    fun setDisplayName_shouldInvokeOnFailCallbackOnFailedChange() {

        val taskMock = mock<Task<Void>>{
            on { addOnFailureListener(any<Activity>(), any()) }
                    .then {
                        (it.getArgument(1) as OnFailureListener).onFailure(mock{})
                        mock<Task<Void>>{}
                    }
        }

        val userMock = mock<FirebaseUser> {
            on { updateProfile(anyOrNull()) }.then {
                mock<Task<Void>> {
                    on { addOnSuccessListener(any<Activity>(), any()) }.then {
                        taskMock
                    }
                }
            }
        }

        val firebaseAuth = mock<FirebaseAuth> { on { currentUser }.then { userMock } }


        val mockBuilder = mock<UserProfileChangeRequest.Builder> {
            on { setDisplayName(any()) }
                    .then { mock<UserProfileChangeRequest.Builder> { } }
        }

        val mockListener = mock<AuthInteractor.OnDisplayNameSetDoneListener> {}

        FirebaseAuthInteractor(firebaseAuth, mockBuilder).setDisplayName(
                "name",
                mockListener,
                mock<Activity> {}
        )

        verify(mockListener, never()).onSuccessNameChange()
        verify(mockListener, times(1)).onSettingDisplayNameFail()
        verify(mockBuilder, times(1)).setDisplayName("name")
        verify(userMock, times(1)).updateProfile(anyOrNull())
    }


    @Test
    fun signOut_shouldDelegateToFirebaseAuth() {

        val firebaseAuth = mock<FirebaseAuth> {  }

        FirebaseAuthInteractor(firebaseAuth).signOut()

        verify(firebaseAuth, times(1)).signOut()
    }

}
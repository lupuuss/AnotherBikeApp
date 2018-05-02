package ga.lupuss.anotherbikeapp.ui.modules.login

import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.pojo.User
import javax.inject.Inject

class LoginPresenter @Inject constructor() : Presenter {

    @Inject
    lateinit var loginView: LoginView

    fun onClickUseWithoutAccount() {

        loginView.getAnotherBikeApp().initMainComponentWithUser(User.defaultUser)
        loginView.startMainActivity()
        loginView.finishActivity()
    }
}
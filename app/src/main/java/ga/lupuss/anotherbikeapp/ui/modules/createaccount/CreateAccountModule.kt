package ga.lupuss.anotherbikeapp.ui.modules.createaccount

import dagger.Module
import dagger.Provides

@Module
class CreateAccountModule(createAccountView: CreateAccountView) {

    val createAccountView = createAccountView
        @Provides
        @CreateAccountScope
        get
}
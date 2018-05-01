package ga.lupuss.anotherbikeapp.di

import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.pojo.User

@Module
class UserModule(currentUser: User) {

    val user = currentUser
        @Provides
        @AnotherBikeAppScope
        get
}
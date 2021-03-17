/**
* AppComponent.kt
*/
@Singleton
@Component(modules = [AppModule::class, BindsModule::class])
interface AppComponent {
    fun inject(app: TUK)
    fun plus(module: UserModule): UserComponent
}

/**
* AppModule.kt
*/
@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    @ApplicationContext
    fun provideApplication(): Context = app
}

/**
* BindsModule.kt
*/
@Module
abstract class BindsModule {

    @Binds
    @Singleton
    internal abstract fun provideGuestUseCase(guestUseCaseImpl: GuestUseCaseImpl): GuestUseCase

}

/**
* UserComponent.kt
*/
@UserScope
@Subcomponent(modules = [UserModule::class])
interface UserComponent {

    fun plus(mainActivityModule: MainActivityModule): MainActivityComponent
  
}

/**
* UserModule.kt
*/
@Module
class UserModule(val user: User) {

    @Provides
    @UserScope
    fun provideUser() = user
  
}

/**
* BaseActivityModule.kt
*/
@Module
abstract class BaseActivityModule<T : BaseActivity>(private val activity: T) {

    @Provides
    @ActivityScope
    fun provideActivity() = activity

}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class UserScope


appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(instance))
                .build()

userComponent = appComponent.plus(UserModule(user))

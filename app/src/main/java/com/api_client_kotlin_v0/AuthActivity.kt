package com.api_client_kotlin_v0

import android.os.Bundle
import android.text.TextUtils.replace
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.api_client_kotlin_v0.R.layout.activity_auth
import com.api_client_kotlin_v0.ui.fragments.LoginFragment
import com.api_client_kotlin_v0.ui.fragments.RegisterFragment
import com.google.android.material.button.MaterialButton

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // По умолчанию показываем экран логина
        supportFragmentManager.beginTransaction()
            .replace(R.id.authFragmentContainer, LoginFragment())
            .commit()
    }
}

package com.fcossetta.simpletest

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.securedevice.core.base.RepoUserCallback
import com.securedevice.core.base.SecureDeviceException
import com.securedevice.core.base.SecurityChecker
import com.securedevice.core.base.SecurityProviderOption
import com.securedevice.core.base.data.NetworkError
import com.securedevice.core.base.model.GithubUser
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private lateinit var securityChecker: SecurityChecker
    private val TAG = "SecurityChecker"
    private val glide: RequestManager by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val builder = SecurityProviderOption.Builder(applicationContext)
        builder.setNetworkClient(SimpleRetrofitClient())
        try {
            securityChecker = SecurityChecker(builder.build())

            userRepo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val repoNameString = repoName
                        .text.toString()
                    search_button.isEnabled = s != null && s.toString().isNotEmpty() &&
                            repoNameString != null && repoNameString.isNotEmpty()
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

            repoName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val userRepoName = userRepo
                        .text.toString()
                    search_button.isEnabled = s != null && s.toString().isNotEmpty() &&
                            userRepoName != null && userRepoName.isNotEmpty()
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
            search_button.setOnClickListener {
                searchGitHubUser(userRepo.text.toString(), repoName.text.toString())
            }
        } catch (c: SecureDeviceException) {
            showUntrusted()
            Log.e(TAG, "Device is not secure,can't proceed")
        } catch (c: Exception) {
            Log.e(TAG, Log.getStackTraceString(c))
        }

    }


    private fun showUntrusted() {
        untrusted_device.visibility = View.VISIBLE
        userRepo.visibility = View.GONE
        repoName.visibility = View.GONE
        search_button.visibility = View.GONE
        users_list.visibility = View.GONE
        empty_view.visibility = View.GONE
    }

    private fun searchGitHubUser(userRepo: String, repoName: String) {
        try {
            val gitHubApi: SecurityChecker.GitHubApi = securityChecker.getGitHubApi()
            gitHubApi.getRepoFavorites(
                userRepo,
                repoName,
                object : RepoUserCallback {
                    override fun onUserFound(users: List<GithubUser>) {
                        Log.d(TAG, "onUserFound: ")
                        users_list.visibility = View.VISIBLE
                        empty_view.visibility = View.GONE
                        val customAdapter = CustomAdapter(users.toTypedArray(), glide)
                        users_list.layoutManager = LinearLayoutManager(
                            applicationContext,
                            RecyclerView.VERTICAL, false
                        )
                        users_list.adapter = customAdapter
                    }

                    override fun onUserNotFound() {
                        Log.d(TAG, "onUserFound: ")
                        users_list.visibility = View.GONE
                        empty_view.visibility = View.VISIBLE
                    }

                    override fun onError(networkError: NetworkError) {
                        Toast.makeText(
                            applicationContext, Log.getStackTraceString(
                                networkError
                                    .exception
                            ), Toast.LENGTH_LONG
                        ).show()
                        Log.d(TAG, "onError: ")
                    }

                })

            val analysisResult = securityChecker.getAnalysisResult()
        } catch (c: SecureDeviceException) {
            Toast.makeText(
                applicationContext, "Device is not trusted. Can't proceed", Toast
                    .LENGTH_LONG
            ).show()
            showUntrusted()
        } catch (c: java.lang.Exception) {
            Log.d(TAG, "onDeviceUntrusted: " + Log.getStackTraceString(c))
        }
    }
}
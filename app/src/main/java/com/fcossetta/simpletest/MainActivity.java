package com.fcossetta.simpletest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.securedevice.core.base.RepoUserCallback;
import com.securedevice.core.base.SecureDeviceException;
import com.securedevice.core.base.SecurityChecker;
import com.securedevice.core.base.SecurityProviderOption;
import com.securedevice.core.base.data.NetworkError;
import com.securedevice.core.base.model.AnalysisResult;
import com.securedevice.core.base.model.GithubUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG = "SecurityChecker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SecurityProviderOption.Builder builder = new SecurityProviderOption.Builder(getApplicationContext());
        builder.setNetworkClient(new SimpleRetrofitClient());
        try {
            SecurityChecker securityChecker = new SecurityChecker(builder.build());
            SecurityChecker.GitHubApi gitHubApi = securityChecker.getGitHubApi();
            gitHubApi.getRepoFavorites("federicocossetta", "SimpleCounties", new RepoUserCallback() {
                @Override
                public void onUserFound(@NonNull List<GithubUser> list) {
                    Log.d(TAG, "onUserFound: ");
                }

                @Override
                public void onUserNotFound() {
                    Log.d(TAG, "onUserFound: ");

                }

                @Override
                public void onError(@NonNull NetworkError networkError) {

                    Log.d(TAG, "onError: ");
                }

                @Override
                public void onDeviceUntrusted(@NonNull SecureDeviceException e) {
                    Log.d(TAG, "onDeviceUntrusted: ");

                }
            });
            AnalysisResult analysisResult = securityChecker.getAnalysisResult();
        }catch (Exception c){

            Log.d(TAG, "onDeviceUntrusted: "+Log.getStackTraceString(c));
        }

    }
}
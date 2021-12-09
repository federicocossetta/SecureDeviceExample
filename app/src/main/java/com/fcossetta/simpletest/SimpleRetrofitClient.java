package com.fcossetta.simpletest;

import androidx.annotation.NonNull;

import com.securedevice.core.base.data.MyNetworkCallback;
import com.securedevice.core.base.data.NetworkClient;
import com.securedevice.core.base.data.NetworkError;
import com.securedevice.core.base.data.NetworkSuccess;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SimpleRetrofitClient  extends NetworkClient {

    @Override
    public void makeNetworkCall(@NonNull String url, @NonNull MyNetworkCallback myNetworkCallback) {
        Api myApi = RetrofitClient.getInstance().getMyApi();
        Call<ResponseBody> objectCall = myApi.makeCall(url);
        objectCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String s = null;
                    try {
                        s = response.body().string();
                        myNetworkCallback.onCallSuccess(new NetworkSuccess(s));
                    } catch (IOException e) {
                        myNetworkCallback.onCallError(new NetworkError(e));
                    }
                } else {
                    if (response.code() == 404) {
                        myNetworkCallback.onCallSuccess(new NetworkSuccess(new ArrayList<>()));
                    } else {
                        try {
                            myNetworkCallback.onCallError(new NetworkError(new Exception(response.errorBody().string())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                myNetworkCallback.onCallError(new NetworkError(t));

            }
        });
    }
}

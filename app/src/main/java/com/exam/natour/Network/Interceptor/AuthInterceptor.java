package com.exam.natour.Network.Interceptor;

import com.exam.natour.Model.AuthUser;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = AuthUser.getInstance().getToken();
        Request req = chain.request();
        if(token != null){
            req = req.newBuilder()
                    .addHeader("Authorization", "Bearer "+token).build();
        }
        return chain.proceed(req);
    }
}

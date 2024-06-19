package com.example.mankomania.apitests;

import com.example.mankomania.api.AuthAPI;

import static org.junit.jupiter.api.Assertions.*;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;

class AuthAPITests {

    @ParameterizedTest
    @ValueSource(strings = {"test@test.at, my_password", ", my_password", "test@test.at, ", ","})
    void testCreateJSONRequest(String emailAndPassword) throws JSONException {
        // splitting generated with ChatGPT since i couldn't figure it out myself :(
        String[] parts = emailAndPassword.split(",", 2);
        String email = parts.length > 0 ? parts[0].trim() : "";
        String password = parts.length > 1 ? parts[1].trim() : "";

        JSONObject result = AuthAPI.createJSONRequest(email, password);

        assertNotNull(result);

        assertEquals(email, result.getString("email"));
        assertEquals(password, result.getString("password"));
    }

    @Test
    void testCreateJSONRequestNull() {
        String email = null;
        String password = null;

        JSONObject result = AuthAPI.createJSONRequest(email, password);

        assertNotNull(result);

        assertFalse(result.has("email"));
        assertFalse(result.has("password"));
    }

    @Test
    void testCreateRequest() throws JSONException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", "test@test.at");
        jsonRequest.put("password", "my_password");
        String path = "/some/path";

        Request request = AuthAPI.createRequest(jsonRequest, path);

        // creating a Request expectedRequest and calling assertEquals(expectedRequest, request);
        // does not work here (AssertionFailedError since the references differ)

        assertEquals("http://se2-demo.aau.at:53214/some/path", request.url().toString());
        assertEquals("POST", request.method());
        assertNotNull(request.body());
    }

    @Test
    void testExecuteLoginRequest_ExceptionThrown() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Call
        Call call = mock(Call.class);
        // set behaviour when enqueue is called
        // use doAnswer instead of when...thenThrow so types match
        // simulate onFailure(..) being called
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, new IOException());
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        // make the okHttpClient return the call
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock LoginCallback
        AuthAPI.LoginCallback callback = mock(AuthAPI.LoginCallback.class);

        // execute request (that should fail)
        AuthAPI.executeLoginRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onLoginSuccess(anyString(), anyString());
        verify(callback).onLoginFailure("Keine Antwort!");
    }

    @Test
    void testExecuteLoginRequest_FailureResponse() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Response that is not successful
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(false);

        // mock Call
        // simulate onResponse(..) being called
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock LoginCallback
        AuthAPI.LoginCallback callback = mock(AuthAPI.LoginCallback.class);

        // execute request
        AuthAPI.executeLoginRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onLoginSuccess(anyString(),anyString());
        verify(callback).onLoginFailure("Falsche Credentials!");
    }


    @Test
    void testExecuteLoginRequest_SuccessfulResponse() throws IOException {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock ResponseBody with token = test_token
        ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.string()).thenReturn("{\"token\":\"test_token\",\"userId\":\"dd66707a-5599-4aa9-babb-e3c92704d852\"}");

        // mock Response that is successful and has responseBody
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);

        // mock Call
        // simulate onResponse(..) being called
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock LoginCallback
        AuthAPI.LoginCallback callback = mock(AuthAPI.LoginCallback.class);

        // execute request
        AuthAPI.executeLoginRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback).onLoginSuccess("test_token", "dd66707a-5599-4aa9-babb-e3c92704d852");
        verify(callback, never()).onLoginFailure(anyString());
    }

    @Test
    void testExecuteRegisterRequest_ExceptionThrown() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Call
        Call call = mock(Call.class);
        // set behaviour when enqueue is called
        // use doAnswer instead of when...thenThrow so types match
        // simulate onFailure(..) being called
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, new IOException());
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        // make the okHttpClient return the call
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock RegisterCallback
        AuthAPI.RegisterCallback callback = mock(AuthAPI.RegisterCallback.class);

        // execute request (that should fail)
        AuthAPI.executeRegisterRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onRegisterSuccess(anyString());
        verify(callback).onRegisterFailure("Keine Antwort!");
    }

    @Test
    void testExecuteRegisterRequest_FailureResponse() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Response that is not successful
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(false);

        // mock Call
        // simulate onResponse(..) being called
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock RegisterCallback
        AuthAPI.RegisterCallback callback = mock(AuthAPI.RegisterCallback.class);

        // execute request
        AuthAPI.executeRegisterRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onRegisterSuccess(anyString());
        verify(callback).onRegisterFailure("User bereits registriert!");
    }


    @Test
    void testExecuteRegisterRequest_SuccessfulResponse() throws IOException {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock ResponseBody with token = test_token
        ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.string()).thenReturn("{\"message\":\"User erfolgreich registriert!\"}");

        // mock Response that is successful and has responseBody
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);

        // mock Call
        // simulate onResponse(..) being called
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock RegisterCallback
        AuthAPI.RegisterCallback callback = mock(AuthAPI.RegisterCallback.class);

        // execute request
        AuthAPI.executeRegisterRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback).onRegisterSuccess("User erfolgreich registriert!");
        verify(callback, never()).onRegisterFailure(anyString());
    }
}

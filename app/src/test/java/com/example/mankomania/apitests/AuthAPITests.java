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

public class AuthAPITests {

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
    void testCreateJSONRequestNull() throws JSONException {
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

        assertEquals("http://10.0.2.2:3000/some/path", request.url().toString());
        assertEquals("POST", request.method());
        assertNotNull(request.body());
    }

    @Test
    void testExecuteRequest_ExceptionThrown() throws IOException {
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

        // mock AuthCallback
        AuthAPI.AuthCallback callback = mock(AuthAPI.AuthCallback.class);

        // execute request (that should fail)
        AuthAPI.executeRequest(okHttpClient, request, "token", "Falsche Credentials!", callback);

        // verify callback
        verify(callback, never()).onSuccess(anyString());
        verify(callback).onFailure("Keine Antwort!");
    }

    @Test
    void testExecuteRequest_FailureResponse() throws IOException {
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

        // mock AuthCallback
        AuthAPI.AuthCallback callback = mock(AuthAPI.AuthCallback.class);

        // execute request
        AuthAPI.executeRequest(okHttpClient, request, "token", "Falsche Credentials!", callback);

        // verify callback
        verify(callback, never()).onSuccess(anyString());
        verify(callback).onFailure("Falsche Credentials!");
    }


    @Test
    void testExecuteRequest_SuccessfulResponse() throws IOException {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock ResponseBody with token = test_token
        ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.string()).thenReturn("{\"token\": \"test_token\"}");

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

        // mock AuthCallback
        AuthAPI.AuthCallback callback = mock(AuthAPI.AuthCallback.class);

        // execute request
        AuthAPI.executeRequest(okHttpClient, request, "token", "Error message", callback);

        // verify callback
        verify(callback).onSuccess("test_token");
        verify(callback, never()).onFailure(anyString());
    }
}

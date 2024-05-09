package com.example.mankomania.apitests;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.mankomania.logik.spieler.Color;
import com.example.mankomania.api.SessionAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SessionAPITests {
    @Test
    void testConvertToEnums() {
        assertEquals(Color.BLUE, SessionAPI.convertToEnums("blue"));
        assertEquals(Color.RED, SessionAPI.convertToEnums("red"));
        assertEquals(Color.GREEN, SessionAPI.convertToEnums("green"));
        assertEquals(Color.PURPLE, SessionAPI.convertToEnums("lila"));

        assertNull(SessionAPI.convertToEnums("invalid_color"));
    }

    @Test
    void testCreateJSONObject_UUID() {
        // prepare input
        UUID lobbyid = UUID.randomUUID();

        // call method
        JSONObject jsonObject = SessionAPI.createJSONObject(lobbyid);

        // assert
        assertTrue(jsonObject.has("lobbyid"));
    }

    @Test
    void testCreateJSONObject_Color() {
        // prepare input
        String color = "Rot";

        // call method
        JSONObject jsonObject = SessionAPI.createJSONObject(color);

        // assert
        assertTrue(jsonObject.has("color"));
    }

    @Test
    void testCreateGetRequest() {
        // set up
        String token = "test_token";
        String path = "/api/session/unavailableColors/1234";

        // call method
        Request request = SessionAPI.createGetRequest(token, path);

        // assert
        assertEquals("http://10.0.2.2:3000/api/session/unavailableColors/1234", request.url().toString());
    }

    @Test
    void testCreatePostRequest() throws JSONException {
        // set up
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("color", "Rot");

        String token = "test_token";
        String path = "/api/session/setColor/1234";

        // call method
        Request request = SessionAPI.createPostRequest(jsonObject, token, path);

        // assert
        assertEquals("http://10.0.2.2:3000/api/session/setColor/1234", request.url().toString());
    }

    @Test
    void testExecuteJoinSessionRequest_FailureResponse() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Response that is not successful
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(false);

        // mock Call
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock JoinSessionCallback
        SessionAPI.JoinSessionCallback callback = mock(SessionAPI.JoinSessionCallback.class);

        // execute request
        SessionAPI.executeJoinSessionRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onJoinSessionSuccess(anyString());
        verify(callback).onJoinSessionFailure(response.message());
    }

    @Test
    void testExecuteGetUnavailableColorsByLobbyRequest_FailureResponse() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Response that is not successful
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(false);

        // mock Call
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock GetUnavailableColorsByLobbyCallback
        SessionAPI.GetUnavailableColorsByLobbyCallback callback = mock(SessionAPI.GetUnavailableColorsByLobbyCallback.class);

        // execute request
        SessionAPI.executeGetUnavailableColorsByLobbyRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onGetUnavailableColorsByLobbySuccess(anyList());
        verify(callback).onGetUnavailableColorsByLobbyFailure(response.message());
    }

    @Test
    void testExecuteGetStatusByLobbyRequest_FailureResponse() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Response that is not successful
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(false);

        // mock Call
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock GetStatusByLobbyCallback
        SessionAPI.GetStatusByLobbyCallback callback = mock(SessionAPI.GetStatusByLobbyCallback.class);

        // execute request
        SessionAPI.executeGetStatusByLobbyRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onGetStatusByLobbySuccess(any(HashMap.class));
        verify(callback).onGetStatusByLobbyFailure(response.message());
    }

    @Test
    void testExecuteSetColorRequest_FailureResponse() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Response that is not successful
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(false);

        // mock Call
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock SetColorCallback
        SessionAPI.SetColorCallback callback = mock(SessionAPI.SetColorCallback.class);

        // execute request
        SessionAPI.executeSetColorRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onSetColorSuccess(anyString());
        verify(callback).onSetColorFailure(response.message());

    }

    @Test
    void testExecuteJoinSessionRequest_ExceptionThrown() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock call
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, new IOException());
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock JoinSessionCallback
        SessionAPI.JoinSessionCallback callback = mock(SessionAPI.JoinSessionCallback.class);

        // execute request
        SessionAPI.executeJoinSessionRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onJoinSessionSuccess(anyString());
        verify(callback).onJoinSessionFailure("Keine Antwort!");
    }

    @Test
    void testExecuteGetUnavailableColorsByLobbyRequest_ExceptionThrown() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock call
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, new IOException());
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock GetUnavailableColorsByLobbyCallback
        SessionAPI.GetUnavailableColorsByLobbyCallback callback = mock(SessionAPI.GetUnavailableColorsByLobbyCallback.class);

        // execute request
        SessionAPI.executeGetUnavailableColorsByLobbyRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onGetUnavailableColorsByLobbySuccess(anyList());
        verify(callback).onGetUnavailableColorsByLobbyFailure("Keine Antwort!");
    }

    @Test
    void testExecuteGetStatusByLobbyRequest_ExceptionThrown() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock call
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, new IOException());
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock GetStatusByLobbyCallback
        SessionAPI.GetStatusByLobbyCallback callback = mock(SessionAPI.GetStatusByLobbyCallback.class);

        // execute request
        SessionAPI.executeGetStatusByLobbyRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onGetStatusByLobbySuccess(any(HashMap.class));
        verify(callback).onGetStatusByLobbyFailure("Keine Antwort!");
    }

    @Test
    void testExecuteSetColorRequest_ExceptionThrown() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock call
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, new IOException());
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock SetColorCallback
        SessionAPI.SetColorCallback callback = mock(SessionAPI.SetColorCallback.class);

        // execute request
        SessionAPI.executeSetColorRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onSetColorSuccess(anyString());
        verify(callback).onSetColorFailure("Keine Antwort!");
    }
}

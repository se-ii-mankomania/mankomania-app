package com.example.mankomania.apitests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

import com.example.mankomania.logik.spieler.Color;
import com.example.mankomania.api.SessionAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
    void testGetColors() throws JSONException {
        // mock JSONArray and JSONObjects
        JSONArray responseArray = mock(JSONArray.class);
        JSONObject jsonObject1 = mock(JSONObject.class);
        JSONObject jsonObject2 = mock(JSONObject.class);
        JSONObject jsonObject3 = mock(JSONObject.class);
        JSONObject jsonObject4 = mock(JSONObject.class);

        // mock JSONArray behavior
        when(responseArray.length()).thenReturn(4);
        when(responseArray.getJSONObject(0)).thenReturn(jsonObject1);
        when(responseArray.getJSONObject(1)).thenReturn(jsonObject2);
        when(responseArray.getJSONObject(2)).thenReturn(jsonObject3);
        when(responseArray.getJSONObject(3)).thenReturn(jsonObject4);

        // mock JSONObject behavior
        when(jsonObject1.getString("color")).thenReturn("blue");
        when(jsonObject2.getString("color")).thenReturn("red");
        when(jsonObject3.getString("color")).thenReturn("green");
        when(jsonObject4.getString("color")).thenReturn("lila");

        String responseBodyString = "[{\"color\":\"blue\"},{\"color\":\"red\"},{\"color\":\"green\"},{\"color\":\"lila\"}]";

        // call method
        List<Color> colors = SessionAPI.getColors(responseBodyString);

        // assert
        assertEquals(4, colors.size());
        assertEquals(Color.BLUE, colors.get(0));
        assertEquals(Color.RED, colors.get(1));
        assertEquals(Color.GREEN, colors.get(2));
        assertEquals(Color.PURPLE, colors.get(3));
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

    @Test
    void testExecuteJoinSessionRequest_SuccessfulResponse() throws IOException {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Response
        ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.string()).thenReturn("{\"message\":\"Joined lobby successfully\"}");

        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);

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
        verify(callback).onJoinSessionSuccess("Joined lobby successfully");
        verify(callback, never()).onJoinSessionFailure(anyString());
    }

    @Test
    void testExecuteGetUnavailableColorsByLobbyRequest_SuccessfulResponse() throws IOException {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Response
        ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.string()).thenReturn("[{\"color\":\"blue\"},{\"color\":\"red\"}]");

        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);

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

        // set up ArgumentCaptor
        ArgumentCaptor<List<Color>> captor = ArgumentCaptor.forClass(List.class);

        // set up List
        List<Color> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);

        // verify callback
        verify(callback).onGetUnavailableColorsByLobbySuccess(captor.capture());
        verify(callback, never()).onGetUnavailableColorsByLobbyFailure(anyString());

        // assert
        assertEquals(colors, captor.getValue());
    }
}

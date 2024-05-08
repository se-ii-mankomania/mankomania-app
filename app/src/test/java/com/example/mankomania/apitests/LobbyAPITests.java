package com.example.mankomania.apitests;

import static org.junit.jupiter.api.Assertions.*;

import com.example.mankomania.api.Lobby;
import com.example.mankomania.api.LobbyAPI;
import com.example.mankomania.api.Status;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LobbyAPITests {

    @Test
    void testGenerateStringArray() {
        List<Lobby> lobbies = new ArrayList<>();
        lobbies.add(new Lobby(null, "Lobby 1", "password1234", true, 4, Status.open));
        lobbies.add(new Lobby(null, "Lobby 2", "", false, 2, Status.open));
        lobbies.add(new Lobby(null, "Lobby 3", "password5678", true, 3, Status.open));

        String[] expectedArray = {
                "P | x/4 | Lobby 1",
                "O | x/2 | Lobby 2",
                "P | x/3 | Lobby 3"
        };

        String[] resultArray = LobbyAPI.generateStringArray(lobbies);

        assertArrayEquals(expectedArray, resultArray);
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    void testAddLobbyToList(String statusName, Status expectedStatus) throws JSONException {
        // prepare JSONObject with the given status
        JSONObject jsonLobby = new JSONObject();
        jsonLobby.put("id", UUID.randomUUID().toString());
        jsonLobby.put("name", "Test Lobby");
        jsonLobby.put("password", "password");
        jsonLobby.put("isprivate", true);
        jsonLobby.put("maxplayers", 4);
        jsonLobby.put("status", statusName);

        List<Lobby> lobbyList = new ArrayList<>();

        // call method
        LobbyAPI.addLobbyToList(jsonLobby, lobbyList);

        // get Lobby for comparison
        Lobby addedLobby = lobbyList.get(0);

        // verify
        assertEquals(1, lobbyList.size());
        assertEquals(expectedStatus, addedLobby.getStatus());
    }

    // data provider for status values and their corresponding Status enum
    // generated by ChatGPT because i suck at this
    private static Object[][] statusProvider() {
        return new Object[][] {
                { "open", Status.open },
                { "starting", Status.starting },
                { "inGame", Status.inGame },
                { "finished", Status.finished },
                { "closed", Status.closed }
        };
    }

    @Test
    void testAddLobbyToList_InvalidStatus() throws JSONException {
        // create a JSONObject with an invalid status
        JSONObject jsonLobby = new JSONObject();
        jsonLobby.put("id", UUID.randomUUID().toString());
        jsonLobby.put("name", "Test Lobby");
        jsonLobby.put("password", "password");
        jsonLobby.put("isprivate", true);
        jsonLobby.put("maxplayers", 4);
        jsonLobby.put("status", "invalid");

        List<Lobby> lobbyList = new ArrayList<>();

        // verify that IllegalArgumentException is thrown for invalid status
        assertThrows(IllegalArgumentException.class, () -> LobbyAPI.addLobbyToList(jsonLobby, lobbyList));
    }

    @Test
    void testCreateJSONLobby_Public() throws JSONException {
        // prepare input
        String name = "Test Lobby";
        String password = null;
        boolean isPrivate = false;
        int maxPlayers = 4;
        Status status = Status.open;

        // call method
        JSONObject jsonLobby = LobbyAPI.createJSONLobby(name, password, isPrivate, maxPlayers, status);

        // verify
        assertNotNull(jsonLobby);
        assertEquals(name, jsonLobby.getString("name"));
        assertEquals(JSONObject.NULL, jsonLobby.get("password"));
        assertEquals(isPrivate, jsonLobby.getBoolean("isPrivate"));
        assertEquals(maxPlayers, jsonLobby.getInt("maxPlayers"));
        assertEquals(status, jsonLobby.get("status"));
    }

    @Test
    void testCreateJSONLobby_Private() throws JSONException {
        // prepare input
        String name = "Test Lobby";
        String password = "password";
        boolean isPrivate = true;
        int maxPlayers = 2;
        Status status = Status.open;

        // call method
        JSONObject jsonLobby = LobbyAPI.createJSONLobby(name, password, isPrivate, maxPlayers, status);

        // verify
        assertNotNull(jsonLobby);
        assertEquals(name, jsonLobby.getString("name"));
        assertEquals(password, jsonLobby.getString("password"));
        assertEquals(isPrivate, jsonLobby.getBoolean("isPrivate"));
        assertEquals(maxPlayers, jsonLobby.getInt("maxPlayers"));
        assertEquals(status, jsonLobby.get("status"));
    }

    @Test
    void testCreateGetRequest_WithStatus() {
        // set up
        String token = "test_token";
        String path = "/api/lobby/getByStatus/";
        Status status = Status.open;

        // call method
        Request request = LobbyAPI.createGetRequest(token, path, status);

        // verify
        assertEquals("http://10.0.2.2:3000/api/lobby/getByStatus/open", request.url().toString());
    }

    @Test
    void testCreateGetRequest_NoStatus() {
        // set up
        String token = "test_token";
        String path = "/api/lobby/getAll";
        Status status = null;

        // call method
        Request request = LobbyAPI.createGetRequest(token, path, status);

        // verify
        assertEquals("http://10.0.2.2:3000/api/lobby/getAll", request.url().toString());
    }

    @Test
    void testCreatePostRequest() throws JSONException {
        // set up
        JSONObject jsonLobby = new JSONObject();
        jsonLobby.put("name", "Test Lobby");
        jsonLobby.put("password", "password");
        jsonLobby.put("isPrivate", true);
        jsonLobby.put("maxPlayers", 2);
        jsonLobby.put("status", Status.open);

        String token = "test_token";
        String path = "/api/lobby/create";

        // call method
        Request request = LobbyAPI.createPostRequest(jsonLobby, token, path);

        // verify
        assertEquals("http://10.0.2.2:3000/api/lobby/create", request.url().toString());
    }

    @Test
    void testExecuteGetRequest_ExceptionThrown() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Call
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, new IOException());
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock AuthCallback
        LobbyAPI.GetLobbiesCallback callback = mock(LobbyAPI.GetLobbiesCallback.class);

        // execute request (that should fail)
        LobbyAPI.executeGetRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onSuccess(any(String[].class), anyList());
        verify(callback).onFailure("Keine Antwort!");
    }

    @Test
    void testExecuteGetRequest_FailureResponse() {
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

        // mock AuthCallback
        LobbyAPI.GetLobbiesCallback callback = mock(LobbyAPI.GetLobbiesCallback.class);

        // execute request
        LobbyAPI.executeGetRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onSuccess(any(String[].class), anyList());
        verify(callback).onFailure(response.message());
    }

    @Test
    void testExecuteGetRequest_SuccessfulResponse() throws IOException {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock successful Response
        ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.string()).thenReturn("[{\"id\":\"23d9eea7-50cf-4b97-a3a0-e5158f4ffec0\",\"name\":\"Test Lobby\",\"password\":\"password\",\"isprivate\":true,\"maxplayers\":4,\"status\":\"open\"}]");

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

        // mock AuthCallback
        LobbyAPI.GetLobbiesCallback callback = mock(LobbyAPI.GetLobbiesCallback.class);

        // execute request
        LobbyAPI.executeGetRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback).onSuccess(any(String[].class), anyList());
        verify(callback, never()).onFailure(anyString());
    }

    @Test
    void testExecutePostRequest_ExceptionThrown() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Call
        Call call = mock(Call.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, new IOException());
            return null;
        }).when(call).enqueue(any());

        // mock Request
        Request request = mock(Request.class);
        when(okHttpClient.newCall(any())).thenReturn(call);

        // mock AuthCallback
        LobbyAPI.AddLobbyCallback callback = mock(LobbyAPI.AddLobbyCallback.class);

        // execute request (that should fail)
        LobbyAPI.executePostRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onSuccess(anyString());
        verify(callback).onFailure("Keine Antwort!");
    }

    @Test
    void testExecutePostRequest_FailureResponse() {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Response
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(false);
        when(response.message()).thenReturn("Some Error");

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

        // mock AuthCallback
        LobbyAPI.AddLobbyCallback callback = mock(LobbyAPI.AddLobbyCallback.class);

        // execute request (that should fail)
        LobbyAPI.executePostRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback, never()).onSuccess(anyString());
        verify(callback).onFailure("Some Error");
    }

    @Test
    void testExecutePostRequest_SuccessResponse() throws IOException {
        // mock OkHttpClient
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        // mock Response
        ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.string()).thenReturn("{\"message\":\"Lobby added successfully\"}");

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

        // mock AuthCallback
        LobbyAPI.AddLobbyCallback callback = mock(LobbyAPI.AddLobbyCallback.class);

        // execute request (that should fail)
        LobbyAPI.executePostRequest(okHttpClient, request, callback);

        // verify callback
        verify(callback).onSuccess("Lobby added successfully");
        verify(callback, never()).onFailure(anyString());
    }
}
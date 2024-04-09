package com.example.mankomania.apitests;

import com.example.mankomania.api.Auth;

import org.junit.Test;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class AuthTests {

    /**
     * need a functioning build.gradle :(
     */
    /*
    @Test
    public void testLoginSuccess() {
        Auth.LoginCallback callback = new Auth.LoginCallback() {
            @Override
            public void onLoginSuccess(String token) {
                assertNotNull(token);
                assertNotEquals("", token);
            }

            @Override
            public void onLoginFailure(String errorMessage) {
                fail("Login should be successful");
            }
        };

        Auth.login("test@test.at", "my_password", callback);
    }
    */
}

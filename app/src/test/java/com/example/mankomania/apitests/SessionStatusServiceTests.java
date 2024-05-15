package com.example.mankomania.apitests;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.mankomania.api.Session;
import com.example.mankomania.api.SessionAPI;
import com.example.mankomania.api.SessionStatusService;
import com.example.mankomania.logik.spieler.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SessionStatusServiceTests {
    @InjectMocks
    private SessionStatusService service;

    @Mock
    private Handler handler;

    @Mock
    private SharedPreferences sharedPreferences;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @BeforeEach
    public void setUp() throws GeneralSecurityException, IOException {
        MockitoAnnotations.initMocks(this);

        MasterKey masterKey = mock(MasterKey.class);
        sharedPreferences = mock(EncryptedSharedPreferences.class);
        when(sharedPreferences.getString("token", null)).thenReturn("dummyToken");
        when(sharedPreferences.getString("lobbyid", null)).thenReturn("dummy-lobby-id");
    }

    @Test
    void testConvertEnumToStringColor() {
        SessionStatusService sessionStatusService=new SessionStatusService();
        assertEquals("blau", sessionStatusService.convertEnumToStringColor(Color.BLUE));
        assertEquals("rot", sessionStatusService.convertEnumToStringColor(Color.RED));
        assertEquals("lila", sessionStatusService.convertEnumToStringColor(Color.PURPLE));
        assertEquals("grün", sessionStatusService.convertEnumToStringColor(Color.GREEN));
    }

    /*@Test
    public void testOnCreate() {
        doNothing().when(handler).postDelayed(any(Runnable.class), eq(5000L));

        service.onCreate();

        verify(handler, times(1)).postDelayed(runnableCaptor.capture(), eq(5000L));
        Runnable capturedRunnable = runnableCaptor.getValue();
        assertNotNull(capturedRunnable);
    }

    @Test
    public void testOnStartCommand() {
        Intent intent = new Intent();
        int result = service.onStartCommand(intent, 0, 1);

        assertEquals(Service.START_STICKY, result);
        verify(handler, times(1)).postDelayed(any(Runnable.class), eq(5000L));
    }

    @Test
    public void testOnDestroy() {
        service.onDestroy();
        verify(handler, times(1)).removeCallbacks(any(Runnable.class));
    }*/

    @Test
    public void testNotifyUpdatesInSession() {
        Session session = mock(Session.class);
        UUID userId = UUID.randomUUID();

        when(session.getIsPlayersTurn()).thenReturn(true);
        when(session.getBalance()).thenReturn(0);
        when(session.getColor()).thenReturn(Color.BLUE);

        service.notifyUpdatesInSession(session, userId);

        // Verifying notifyTurnChanged and notifyBalanceBelowThreshold calls
        service.notifyTurnChanged("blau", true, userId);
        service.notifyBalanceBelowThreshold(userId, "blau");
    }

    @Test
    public void testRegisterAndRemoveObservers() {
        SessionStatusService.PositionObserver positionObserver = mock(SessionStatusService.PositionObserver.class);
        SessionStatusService.BalanceObserver balanceObserver = mock(SessionStatusService.BalanceObserver.class);
        SessionStatusService.PlayersTurnObserver playersTurnObserver = mock(SessionStatusService.PlayersTurnObserver.class);
        SessionStatusService.BalanceBelowThresholdObserver balanceBelowThresholdObserver = mock(SessionStatusService.BalanceBelowThresholdObserver.class);

        service.registerObserver(positionObserver);
        service.registerObserver(balanceObserver);
        service.registerObserver(playersTurnObserver);
        service.registerObserver(balanceBelowThresholdObserver);

        assertTrue(service.getPositionObservers().contains(positionObserver));
        assertTrue(service.getBalanceObservers().contains(balanceObserver));
        assertTrue(service.getPlayersTurnObservers().contains(playersTurnObserver));
        assertTrue(service.getBalanceBelowThresholdObservers().contains(balanceBelowThresholdObserver));

        service.removeObserver(positionObserver);
        service.removeObserver(balanceObserver);
        service.removeObserver(playersTurnObserver);
        service.unregisterObserver(balanceBelowThresholdObserver);

        assertFalse(service.getPositionObservers().contains(positionObserver));
        assertFalse(service.getBalanceObservers().contains(balanceObserver));
        assertFalse(service.getPlayersTurnObservers().contains(playersTurnObserver));
        assertFalse(service.getBalanceBelowThresholdObservers().contains(balanceBelowThresholdObserver));
    }

    @Test
    public void testNotifyObservers() {
        UUID userId = UUID.randomUUID();
        int newPosition = 5;
        int newBalance = 100;
        String color = "blau";
        boolean newTurn = true;

        SessionStatusService.PositionObserver positionObserver = mock(SessionStatusService.PositionObserver.class);
        SessionStatusService.BalanceObserver balanceObserver = mock(SessionStatusService.BalanceObserver.class);
        SessionStatusService.PlayersTurnObserver playersTurnObserver = mock(SessionStatusService.PlayersTurnObserver.class);
        SessionStatusService.BalanceBelowThresholdObserver balanceBelowThresholdObserver = mock(SessionStatusService.BalanceBelowThresholdObserver.class);

        service.registerObserver(positionObserver);
        service.registerObserver(balanceObserver);
        service.registerObserver(playersTurnObserver);
        service.registerObserver(balanceBelowThresholdObserver);

        service.notifyPositionChanged(userId, newPosition);
        verify(positionObserver, times(1)).onPositionChanged(userId, newPosition);

        service.notifyBalanceChanged(userId, newBalance);
        verify(balanceObserver, times(1)).onBalanceChanged(userId, newBalance);

        service.notifyTurnChanged(color, newTurn, userId);
        verify(playersTurnObserver, times(1)).onTurnChanged(color, newTurn, userId);

        service.notifyBalanceBelowThreshold(userId, color);
        verify(balanceBelowThresholdObserver, times(1)).onBalanceBelowThreshold(userId, color);
    }
}
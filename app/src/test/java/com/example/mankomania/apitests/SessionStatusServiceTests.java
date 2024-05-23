package com.example.mankomania.apitests;

import android.app.Service;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.security.crypto.EncryptedSharedPreferences;

import com.example.mankomania.api.PlayerSession;
import com.example.mankomania.api.SessionStatusService;
import com.example.mankomania.logik.spieler.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SessionStatusServiceTests {
    @InjectMocks
    private SessionStatusService service;

    @Mock
    private SharedPreferences sharedPreferences;


    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        sharedPreferences = mock(EncryptedSharedPreferences.class);
        when(sharedPreferences.getString("token", null)).thenReturn("dummyToken");
        when(sharedPreferences.getString("lobbyid", null)).thenReturn("dummy-lobby-id");
    }

    @Test
    void testConvertEnumToStringColor() {
        SessionStatusService sessionStatusService=SessionStatusService.getInstance();
        assertEquals("blau", sessionStatusService.convertEnumToStringColor(Color.BLUE));
        assertEquals("rot", sessionStatusService.convertEnumToStringColor(Color.RED));
        assertEquals("lila", sessionStatusService.convertEnumToStringColor(Color.PURPLE));
        assertEquals("grün", sessionStatusService.convertEnumToStringColor(Color.GREEN));
    }

    @Test
    void testOnStartCommand() {
        Handler handlerMock=mock(Handler.class);

        when(handlerMock.postDelayed(any(Runnable.class), eq(5000L))).thenReturn(true);

        SessionStatusService sessionStatusService = SessionStatusService.getInstance();
        sessionStatusService.setHandler(handlerMock);

        int expectedResult = Service.START_STICKY;
        int actualResult = sessionStatusService.onStartCommand(null, 0, 1);

        assertEquals(expectedResult, actualResult);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);
        verify(handlerMock, times(1)).postDelayed(runnableCaptor.capture(), delayCaptor.capture());
        assertEquals(5000L, delayCaptor.getValue());
        assertEquals(sessionStatusService.getRunnable(), runnableCaptor.getValue());
    }

    @Test
    void testNotifyUpdatesInSession() {
        PlayerSession playerSession = mock(PlayerSession.class);
        UUID userId = UUID.randomUUID();

        when(playerSession.getIsPlayersTurn()).thenReturn(true);
        when(playerSession.getBalance()).thenReturn(0);
        when(playerSession.getColor()).thenReturn(Color.BLUE);

        service.notifyUpdatesInSession(playerSession, userId);

        service.notifyTurnChanged("blau", true, userId);
        service.notifyBalanceBelowThreshold(userId, "blau");
    }

    @Test
    void testRegisterAndRemoveObservers() {
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
    void testNotifyObservers() {
        UUID userId = UUID.randomUUID();
        int newPosition = 5;
        int newBalance = 100;
        String color = "blau";
        boolean newTurn = true;
        PlayerSession playerSession =new PlayerSession(userId,"email",Color.BLUE,newPosition,newBalance,0,0,0,newTurn);

        SessionStatusService.PositionObserver positionObserver = mock(SessionStatusService.PositionObserver.class);
        SessionStatusService.BalanceObserver balanceObserver = mock(SessionStatusService.BalanceObserver.class);
        SessionStatusService.PlayersTurnObserver playersTurnObserver = mock(SessionStatusService.PlayersTurnObserver.class);
        SessionStatusService.BalanceBelowThresholdObserver balanceBelowThresholdObserver = mock(SessionStatusService.BalanceBelowThresholdObserver.class);

        service.registerObserver(positionObserver);
        service.registerObserver(balanceObserver);
        service.registerObserver(playersTurnObserver);
        service.registerObserver(balanceBelowThresholdObserver);

        service.notifyPositionChanged(playerSession);
        verify(positionObserver, times(1)).onPositionChanged(playerSession);

        service.notifyBalanceChanged(userId, newBalance);
        verify(balanceObserver, times(1)).onBalanceChanged(userId, newBalance);

        service.notifyTurnChanged(color, newTurn, userId);
        verify(playersTurnObserver, times(1)).onTurnChanged(color, newTurn, userId);

        service.notifyBalanceBelowThreshold(userId, color);
        verify(balanceBelowThresholdObserver, times(1)).onBalanceBelowThreshold(userId, color);
    }
}
package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.UpdateUsernameUseCaseSync.UseCaseResult;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UpdateUsernameUseCaseSyncTest {
    static final String USERNAME = "test username";
    static final String USER_ID = "testUserId";

    UpdateUsernameUseCaseSync SUT;


    @Mock UpdateUsernameHttpEndpointSync mEndpoint;
    @Mock UsersCache mCache;
    @Mock EventBusPoster mPoster;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        SUT = new UpdateUsernameUseCaseSync(mEndpoint, mCache, mPoster);
        success();
    }

    // if success - username and userid are passed to endpoint

    @Test
    public void updateUsernameSync_success_InfoPassedToEndpoint()
        throws NetworkErrorException {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mEndpoint).updateUsername(ac.capture(), ac.capture());
        List<String> captures = ac.getAllValues();
        assertThat(captures.get(0), is(USER_ID));
        assertThat(captures.get(1), is(USERNAME));
    }

    // if success - username and userid are cached

    @Test
    public void updateUsernameSync_success_InfoCached() throws NetworkErrorException{
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mCache).cacheUser(ac.capture());
        User captures = ac.getValue();
        assertThat(captures.getUserId(), is(USER_ID));
        assertThat(captures.getUsername(), is(USERNAME));
    }

    // if success - event posted

    @Test
    public void updateUsernameSync_success_eventPosted() throws NetworkErrorException{
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mPoster).postEvent(ac.capture());
        assertThat(ac.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    // if failed - event not posted

    @Test
    public void updateUsernameSync_authError_eventNotPosted() throws NetworkErrorException{
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mPoster);
    }

    @Test
    public void updateUsernameSync_serverError_eventNotPosted() throws NetworkErrorException{
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mPoster);
    }

    @Test
    public void updateUsernameSync_generalError_eventNotPosted() throws NetworkErrorException{
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mPoster);
    }
    // if auth_error at endpoint, no cache

    @Test
    public void updateUsernameSync_authError_userNotCached() throws NetworkErrorException{
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mCache);
    }

    // if server_err at endpoint, no cache
    @Test
    public void updateUsernameSync_serverError_userNotCached() throws NetworkErrorException{
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mCache);
    }

    // if general_error at endpoint, no cache
    @Test
    public void updateUsernameSync_generalError_userNotCached() throws NetworkErrorException{
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mCache);
    }
    // if success, success returned

    @Test
    public void updateUsernameSync_success_successReturned() throws NetworkErrorException{
        UseCaseResult status = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(status, is(UseCaseResult.SUCCESS));
    }

    // if auth_error, failure returned

    @Test
    public void updateUsernameSync_authError_failureReturned() throws NetworkErrorException{
        authError();
        UseCaseResult status = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(status, is(UseCaseResult.FAILURE));
    }

    // if server_error, failure returned
    @Test
    public void updateUsernameSync_serverError_failureReturned() throws NetworkErrorException{
        serverError();
        UseCaseResult status = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(status, is(UseCaseResult.FAILURE));
    }
    // if general_error, failure returned
    @Test
    public void updateUsernameSync_generalError_failureReturned() throws NetworkErrorException{
        generalError();
        UseCaseResult status = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(status, is(UseCaseResult.FAILURE));
    }
    // if network error, network error returned
    @Test
    public void updateUsernameSync_networkError_networkErrorReturned() throws NetworkErrorException{
        networkError();
        UseCaseResult status = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(status, is(UseCaseResult.NETWORK_ERROR));
    }

    // helper functions

    private void success() throws NetworkErrorException {
        when(mEndpoint.updateUsername(any(String.class), any(String.class)))
            .thenReturn(new EndpointResult(
                EndpointResultStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void authError() throws NetworkErrorException {
        when(mEndpoint.updateUsername(any(String.class), any(String.class))).thenReturn(
            new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void serverError() throws NetworkErrorException {
        when(mEndpoint.updateUsername(any(String.class), any(String.class))).thenReturn(
            new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void generalError() throws NetworkErrorException {
        when(mEndpoint.updateUsername(any(String.class), any(String.class))).thenReturn(
            new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void networkError() throws NetworkErrorException{
        when(mEndpoint.updateUsername(any(String.class), any(String.class))).thenThrow(
            new NetworkErrorException());
    }
}
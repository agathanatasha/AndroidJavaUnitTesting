package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status;
import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class FetchUserUseCaseSyncTest {

    public static final String USER_ID = "testUserId";
    public static final String USERNAME = "testUsername";

    FetchUserUseCaseSync SUT;

    @Mock FetchUserHttpEndpointSync mEndpoint;
    @Mock UsersCache mCache;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        SUT = new FetchUserUseCaseSync(mEndpoint, mCache);
        success();
    }

    @Test
    public void fetchUserSync_success_userInfoPassedToEndpoint() throws NetworkErrorException {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.fetchUserSync(USER_ID);
        verify(mEndpoint).fetchUserSync(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }

    @Test
    public void fetchUserSync_success_userCached() throws NetworkErrorException {
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.fetchUserSync(USER_ID);
        verify(mCache).cacheUser(ac.capture());
        User cachedUser = ac.getValue();
        assertThat(cachedUser.getUserId(), is(USER_ID));
        assertThat(cachedUser.getUsername(), is(USERNAME));
    }

    @Test
    public void fetchUserSync_success_endpointNotPolled() throws NetworkErrorException {
        cachedUser();
        SUT.fetchUserSync(USER_ID);
        verifyNoMoreInteractions(mEndpoint);
        verify(mCache).getUser(USER_ID);
    }

    @Test
    public void fetchUserSync_success_cachedUserReturned() throws NetworkErrorException {
        cachedUser();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser().getUserId(), is(USER_ID));
        assertThat(result.getUser().getUsername(), is(USERNAME));
    }

    @Test
    public void fetchUserSync_authError_userNotCached() throws NetworkErrorException {
        authError();
        SUT.fetchUserSync(USER_ID);
        verify(mCache, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_generalError_userNotCached() throws NetworkErrorException {
        generalError();
        SUT.fetchUserSync(USER_ID);
        verify(mCache, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_success_successReturned() throws NetworkErrorException {
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(Status.SUCCESS));
    }

    @Test
    public void fetchUserSync_authError_failureReturned() throws NetworkErrorException {
        authError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    @Test
    public void fetchUserSync_authError_nullUserReturned() throws NetworkErrorException {
        authError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUserSync_generalError_failureReturned() throws NetworkErrorException {
        generalError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    @Test
    public void fetchUserSync_generalError_nullUserReturned() throws NetworkErrorException {
        generalError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUserSync_networkError_networkErrorReturned() throws NetworkErrorException {
        networkError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(Status.NETWORK_ERROR));
    }

    // region helper method
    private void success() throws NetworkErrorException {
        when(mEndpoint.fetchUserSync(any(String.class))).thenReturn(new EndpointResult(
            EndpointStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void authError() throws NetworkErrorException {
        when(mEndpoint.fetchUserSync(any(String.class))).thenReturn(
            new EndpointResult(EndpointStatus.AUTH_ERROR, "", ""));
    }

    private void generalError() throws NetworkErrorException{
        when(mEndpoint.fetchUserSync(any(String.class))).thenReturn(
            new EndpointResult(EndpointStatus.GENERAL_ERROR, "", ""));
    }
    
    private void networkError() throws NetworkErrorException {
        when(mEndpoint.fetchUserSync(any(String.class))).thenThrow(new NetworkErrorException());
    }

    private void cachedUser() {
        when(mCache.getUser(any(String.class))).thenReturn(new User(USER_ID, USERNAME));
    }

    // endregion helper method
}
package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;
import com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.UseCaseResult;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTest {
    static String USER_ID = "test123";
    static String FULL_NAME = "fullname";
    static String IMAGE_URL = "/images/image.jpg";

    UserProfileHttpEndpointSyncTd mEndpoint;
    UsersCacheTd mCache;
    FetchUserProfileUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        mEndpoint = new UserProfileHttpEndpointSyncTd();
        mCache = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(mEndpoint, mCache);
    }

    @Test
    public void fetchUserProfileSync_success_UserIdPassedToEndpoint() {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mEndpoint.mUserId, is(USER_ID));
    }

    // if success - usercaseresult success is returned

    @Test
    public void fetchUserProfileSync_success_successReturned() {
        UseCaseResult state = SUT.fetchUserProfileSync(USER_ID);
        assertThat(state, is(UseCaseResult.SUCCESS));
    }

    // if failed - usercaseresult failure is returned

    @Test
    public void fetchUserProfileSync_serverError_failureReturned() {
        mEndpoint.mServerError = true;
        UseCaseResult state = SUT.fetchUserProfileSync(USER_ID);
        assertThat(state, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_generalError_failureReturned() {
        mEndpoint.mGeneralError = true;
        UseCaseResult state = SUT.fetchUserProfileSync(USER_ID);
        assertThat(state, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_authError_failureReturned() {
        mEndpoint.mAuthError = true;
        UseCaseResult state = SUT.fetchUserProfileSync(USER_ID);
        assertThat(state, is(UseCaseResult.FAILURE));
    }

    // if failed - usercaseresult network_error is returned

    @Test
    public void fetchUserProfileSync_networkError_failureReturned() {
        mEndpoint.mNetworkError = true;
        UseCaseResult state = SUT.fetchUserProfileSync(USER_ID);
        assertThat(state, is(UseCaseResult.NETWORK_ERROR));
    }

    // if success - cache is updated

    @Test
    public void fetchUserProfileSync_success_userCached() {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mCache.getUser(USER_ID).getUserId(), is(USER_ID));
        assertThat(mCache.getUser(USER_ID).getFullName(), is(FULL_NAME));
        assertThat(mCache.getUser(USER_ID).getImageUrl(), is(IMAGE_URL));
    }

    // http auth_error, user not cached

    @Test
    public void fetchUserProfileSync_authError_userNotCached() {
        mEndpoint.mAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mCache.getUser(USER_ID), is(nullValue()));
    }

    // http server_error, user not cached

    @Test
    public void fetchUserProfileSync_serverError_userNotCached() {
        mEndpoint.mServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mCache.getUser(USER_ID), is(nullValue()));
    }

    // http general_error, user not cached

    @Test
    public void fetchUserProfileSync_generalError_userNotCached() {
        mEndpoint.mGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mCache.getUser(USER_ID), is(nullValue()));
    }

    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {
        public String mUserId;
        public boolean mAuthError;
        public boolean mServerError;
        public boolean mGeneralError;
        public boolean mNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;
            if (mAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, userId, "", "");
            } else if (mServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, userId, "", "");
            } else if (mGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, userId, "", "");
            } else if (mNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, FULL_NAME, IMAGE_URL);
            }
        }
    }

    private static class UsersCacheTd implements UsersCache {
        User cachedUser;

        @Override
        public void cacheUser(User user) {
            cachedUser = user;
        }

        @Nullable
        @Override
        public User getUser(final String userId) {
            if (cachedUser != null) {
                if (cachedUser.getUserId().equals(userId)) {
                    return cachedUser;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}

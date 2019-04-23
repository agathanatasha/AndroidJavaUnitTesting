package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;
import org.jetbrains.annotations.Nullable;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import sun.nio.ch.Net;

public class FetchUserUseCaseSync {

    enum Status {
        SUCCESS, FAILURE, NETWORK_ERROR
    }

    class UseCaseResult {
        private final Status mStatus;

        @Nullable private final User mUser;

        public UseCaseResult(Status status, @Nullable User user) {
            mStatus = status;
            mUser = user;
        }

        public Status getStatus() {
            return mStatus;
        }

        @Nullable
        public User getUser() {
            return mUser;
        }
    }

    FetchUserHttpEndpointSync endpoint;
    UsersCache cache;

    public FetchUserUseCaseSync(FetchUserHttpEndpointSync endpointSync, UsersCache cache) {
        this.endpoint = endpointSync;
        this.cache = cache;
    }

    UseCaseResult fetchUserSync(String userId) throws NetworkErrorException {
        User cachedUser = cache.getUser(userId);
        if (cachedUser != null) {
            return new UseCaseResult(Status.SUCCESS, cachedUser);
        } else {
            try {
                EndpointResult result = endpoint.fetchUserSync(userId);
                User returnedUser;
                switch (result.getStatus()) {
                    case SUCCESS:
                        returnedUser = new User(result.getUserId(), result.getUsername());
                        cache.cacheUser(returnedUser);
                        return new UseCaseResult(Status.SUCCESS, returnedUser);

                    case AUTH_ERROR:
                        return new UseCaseResult(Status.FAILURE, null);
                    case GENERAL_ERROR:
                        return new UseCaseResult(Status.FAILURE, null);
                }
            } catch (NetworkErrorException e) {
                return new UseCaseResult(Status.NETWORK_ERROR, null);
            }
        }
        return new UseCaseResult(Status.FAILURE, null);
    }
}

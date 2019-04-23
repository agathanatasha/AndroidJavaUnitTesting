package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.ReputationUseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.Status.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class FetchReputationUseCaseSyncTest {
    public static final int REPUTATION = 10;

    // region constants
    // endregion constants

    FetchReputationUseCaseSync SUT;

    @Mock GetReputationHttpEndpointSync mEndpoint;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        SUT = new FetchReputationUseCaseSync(mEndpoint);
        success();
    }

    @Test
    public void fetchReputationUseCase_success_successReturned() {
        ReputationUseCaseResult result = SUT.fetchReputationUseCase();
        assertThat(result.getStatus(), is(SUCCESS));
    }

    @Test
    public void fetchReputationUseCase_success_reputationReturned() {
        ReputationUseCaseResult result = SUT.fetchReputationUseCase();
        assertThat(result.getReputation(), is(REPUTATION));
    }

    @Test
    public void fetchReputationUseCase_generalError_failureReturned() {
        generalError();
        ReputationUseCaseResult result = SUT.fetchReputationUseCase();
        assertThat(result.getStatus(), is(FAILURE));
    }

    @Test
    public void fetchReputationUseCase_generalError_reputationIsZero() {
        generalError();
        ReputationUseCaseResult result = SUT.fetchReputationUseCase();
        assertThat(result.getReputation(), is(0));
    }

    @Test
    public void fetchReputationUseCase_networkError_failureReturned() {
        networkError();
        ReputationUseCaseResult result = SUT.fetchReputationUseCase();
        assertThat(result.getStatus(), is(FAILURE));
    }

    @Test
    public void fetchReputationUseCase_networkError_reputationIsZero() {
        networkError();
        ReputationUseCaseResult result = SUT.fetchReputationUseCase();
        assertThat(result.getReputation(), is(0));
    }

    // region helper methods
    private void success() {
        when(mEndpoint.getReputationSync()).thenReturn(new EndpointResult(EndpointStatus.SUCCESS, REPUTATION)
        );
    }


    private void generalError() {
        when(mEndpoint.getReputationSync()).thenReturn(new EndpointResult(EndpointStatus.GENERAL_ERROR,0));
    }
    
    private void networkError() {
        when(mEndpoint.getReputationSync()).thenReturn(
            new EndpointResult(EndpointStatus.NETWORK_ERROR, 0));
    }
    // endregion helper methods
}
package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;

public class FetchReputationUseCaseSync {
    public enum Status {
        SUCCESS,
        FAILURE
    }

    static class ReputationUseCaseResult {
        private Status status;
        private Integer reputation;

        public ReputationUseCaseResult(Status status, Integer reputation) {
            this.status = status;
            this.reputation = reputation;
        }

        public Status getStatus() {
            return status;
        }

        public Integer getReputation() {
            return reputation;
        }
    }
    GetReputationHttpEndpointSync endpoint;

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync endpoint) {
        this.endpoint = endpoint;
    }

    public ReputationUseCaseResult fetchReputationUseCase() {
        EndpointResult endpointResult = endpoint.getReputationSync();
        switch (endpointResult.getStatus()) {
            case SUCCESS:
                return new ReputationUseCaseResult(Status.SUCCESS,endpointResult.getReputation());
            case GENERAL_ERROR:
            case NETWORK_ERROR:
                return new ReputationUseCaseResult(Status.FAILURE, 0);

        }
        throw new RuntimeException("Unknown result" + endpointResult);
    };
}

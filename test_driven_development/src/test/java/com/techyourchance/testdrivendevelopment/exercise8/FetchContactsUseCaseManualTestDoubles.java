package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason;
import java.util.ArrayList;
import java.util.List;
import jdk.nashorn.internal.codegen.CompilerConstants.Call;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class FetchContactsUseCaseManualTestDoubles {
    // region constants
    public static final String FILTER_TERM = "test contact";
    public static final String CONTACT_ID = "testId";
    public static final String CONTACT_FULL_NAME = "test full name";
    public static final String CONTACT_PHONE_NUM = "416-000-0000";
    public static final String CONTACT_IMAGE_URL = "/test/test_image.jpg";
    public static final int CONTACT_AGE = 30;
    // endregion constants

    // region mocks
    GetContactsHttpEndpointTd mEndpoint;
    @Mock FetchContactsUseCase.Listener mListener1;
    @Mock FetchContactsUseCase.Listener mListener2;

    @Captor ArgumentCaptor<List<Contact>> listContactCaptor;
    // endregion mocks

    FetchContactsUseCase SUT;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mEndpoint = new GetContactsHttpEndpointTd();
        SUT = new FetchContactsUseCase(mEndpoint);
    }

    @Test
    public void fetchContact_success_termPassedToEndpoint() {
        SUT.fetchContact(FILTER_TERM);
        assertThat(mEndpoint.invocationCount, is(1));
        assertThat(mEndpoint.lastFilterTerm, is(FILTER_TERM));
    }

    @Test
    public void fetchContact_success_registeredListenerNotifiedWithCorrectContacts() {
        // Arrange
        // Act
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        SUT.fetchContact(FILTER_TERM);
        verify(mListener1).onContactsFetched(listContactCaptor.capture());
        verify(mListener2).onContactsFetched(listContactCaptor.capture());
        List<List<Contact>> captures = listContactCaptor.getAllValues();
        List<Contact> capture1 = captures.get(0);
        List<Contact> capture2 = captures.get(1);
        // Assert
        assertThat(capture1, is(getContacts()));
        assertThat(capture2, is(getContacts()));
    }

    @Test
    public void fetchContact_success_unregisteredListenerNotNotified() {
        // Arrange
        // Act
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        SUT.unregisterListener(mListener2);
        SUT.fetchContact(FILTER_TERM);
        // Assert
        verify(mListener1).onContactsFetched(any(List.class));
        verifyNoMoreInteractions(mListener2);
    }

    @Test
    public void fetchContact_generalError_registeredListenersNotifiedOfFailure() {
        // Arrange
        mEndpoint.generalError = true;
        // Act
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        SUT.fetchContact(FILTER_TERM);
        // Assert
        verify(mListener1).onFetchContactsFailed(FailReason.GENERAL_ERROR);
        verify(mListener2).onFetchContactsFailed(FailReason.GENERAL_ERROR);
    }

    @Test
    public void fetchContact_networkError_registeredListenersNotifiedOfFailure() {
        // Arrange
        mEndpoint.networkError = true;
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        SUT.fetchContact(FILTER_TERM);
        // Assert
        verify(mListener1).onFetchContactsFailed(FailReason.NETWORK_ERROR);
        verify(mListener2).onFetchContactsFailed(FailReason.NETWORK_ERROR);
    }

    // region helper methods
    private List<ContactSchema> getContactsSchemes() {
        List<ContactSchema> schemas = new ArrayList<>();
        schemas.add(
            new ContactSchema(CONTACT_ID, CONTACT_FULL_NAME, CONTACT_PHONE_NUM, CONTACT_IMAGE_URL,
                CONTACT_AGE));
        return schemas;
    }

    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(CONTACT_ID, CONTACT_FULL_NAME, CONTACT_IMAGE_URL));
        return contacts;
    }
    // endregion helper methods

    // region helper class
    private class GetContactsHttpEndpointTd implements GetContactsHttpEndpoint {
        public boolean generalError;
        public boolean networkError;
        private int invocationCount;
        private String lastFilterTerm;

        @Override
        public void getContacts(String filterTerm, Callback callback) {
            invocationCount++;
            lastFilterTerm = filterTerm;
            if (generalError) {
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);
            } else if (networkError) {
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);
            } else {
                callback.onGetContactsSucceeded(getContactsSchemes());
            }
        }
    }
    // endregion helper class
}
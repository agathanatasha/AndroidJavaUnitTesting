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

public class FetchContactsUseCaseTest {
    // region constants
    public static final String FILTER_TERM = "test contact";
    public static final String CONTACT_ID = "testId";
    public static final String CONTACT_FULL_NAME = "test full name";
    public static final String CONTACT_PHONE_NUM = "416-000-0000";
    public static final String CONTACT_IMAGE_URL = "/test/test_image.jpg";
    public static final int CONTACT_AGE = 30;
    // endregion constants

    // region mocks
    @Mock GetContactsHttpEndpoint mEndpoint;
    @Mock FetchContactsUseCase.Listener mListener1;
    @Mock FetchContactsUseCase.Listener mListener2;

    @Captor ArgumentCaptor<List<Contact>> listContactCaptor;
    // endregion mocks

    FetchContactsUseCase SUT;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        SUT = new FetchContactsUseCase(mEndpoint);
        success();
    }

    @Test
    public void fetchContact_success_termPassedToEndpoint() {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.fetchContact(FILTER_TERM);
        verify(mEndpoint).getContacts(ac.capture(), any(Callback.class));
        assertThat(ac.getValue(), is(FILTER_TERM));
    }

    @Test
    public void fetchContact_success_registeredListenersNotifiedWithCorrectContacts() {
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
    public void fetchContact_success_unregisteredListenersNotNotified() {
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
        generalError();
        ArgumentCaptor<FailReason> ac = ArgumentCaptor.forClass(FailReason.class);
        // Act
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        SUT.fetchContact(FILTER_TERM);
        verify(mListener1).onFetchContactsFailed(ac.capture());
        verify(mListener2).onFetchContactsFailed(ac.capture());
        List<FailReason> captures = ac.getAllValues();
        FailReason capture1 = captures.get(0);
        FailReason capture2 = captures.get(1);
        // Assert
        assertThat(capture1, is(FailReason.GENERAL_ERROR));
        assertThat(capture2, is(FailReason.GENERAL_ERROR));
    }

    @Test
    public void fetchContact_networkError_registeredListenersNotifiedOfFailure() {
        // Arrange
        networkError();
        ArgumentCaptor<FailReason> ac = ArgumentCaptor.forClass(FailReason.class);
        // Act
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        SUT.fetchContact(FILTER_TERM);
        verify(mListener1).onFetchContactsFailed(ac.capture());
        verify(mListener2).onFetchContactsFailed(ac.capture());
        List<FailReason> captures = ac.getAllValues();
        FailReason capture1 = captures.get(0);
        FailReason capture2 = captures.get(1);
        // Assert
        assertThat(capture1, is(FailReason.NETWORK_ERROR));
        assertThat(capture2, is(FailReason.NETWORK_ERROR));
    }

    // region helper methods
    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactsSchemes());
                return null;
            }
        }).when(mEndpoint).getContacts(any(String.class), any(Callback.class));
    }

    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(mEndpoint).getContacts(any(String.class), any(Callback.class));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(mEndpoint).getContacts(any(String.class), any(Callback.class));
    }

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
}
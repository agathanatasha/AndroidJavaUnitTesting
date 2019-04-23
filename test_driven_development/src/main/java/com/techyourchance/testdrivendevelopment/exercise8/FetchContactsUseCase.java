package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason;
import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase {
    private final List<Listener> listeners = new ArrayList<>();
    private GetContactsHttpEndpoint endpoint;

    public FetchContactsUseCase(GetContactsHttpEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public void fetchContact(String filterTerm) {
        endpoint.getContacts(filterTerm, new Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contacts) {
                for (Listener listener : listeners) {
                    listener.onContactsFetched(contactsFromSchemas(contacts));
                }
            }

            @Override
            public void onGetContactsFailed(FailReason failReason) {
                for (Listener listener : listeners) {
                    listener.onFetchContactsFailed(failReason);
                }
            }
        });
    }

    private List<Contact> contactsFromSchemas(List<ContactSchema> contactSchemas) {
        List<Contact> contactsList = new ArrayList<>();
        for (ContactSchema schema : contactSchemas) {
            contactsList.add(
                new Contact(schema.getId(), schema.getFullName(), schema.getImageUrl()));
        }
        return contactsList;
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    public interface Listener {
        void onContactsFetched(List<Contact> capture);
        void onFetchContactsFailed(FailReason capture);
    }
}

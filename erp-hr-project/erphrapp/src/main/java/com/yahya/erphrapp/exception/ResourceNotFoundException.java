package com.yahya.erphrapp.exception;


// this error is thrown whenever we cannot find an item by its ID in the db
// status code = 404 Not Found
public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " with id " + id + " not found"  );
    }
}

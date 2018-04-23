package com.examples.whywait.login;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

public class Backend
{

    private java.util.Date updated;
    private String restName;
    private String guestName;
    private String objectId;
    private String name;
    private String ownerId;
    private Integer partySize;
    private java.util.Date created;


    public java.util.Date getUpdated()
    {
        return updated;
    }

    public String getRestName()
    {
        return restName;
    }

    public void setRestName( String RestaurantName )
    {
        this.restName = RestaurantName;
    }

    public String getGuestName()
    {
        return guestName;
    }

    public void setGuestName( String guestName )
    {
        this.guestName = guestName;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public String getOwnerId()
    {
        return ownerId;
    }



    public String getName(){ return name;}

    public void setName(String name){ this.name = name; }

    public int getPartySize(){ return partySize;}

    public void setPartySize(int partySize){ this.partySize = partySize; }

    public java.util.Date getCreated()
    {
        return created;
    }


    public Backend save()
    {
        return Backendless.Data.of( Backend.class ).save( this );
    }

    public void saveAsync( AsyncCallback<Backend> callback )
    {
        Backendless.Data.of( Backend.class ).save( this, callback );
    }

    public Long remove()
    {
        return Backendless.Data.of( Backend.class ).remove( this );
    }

    public void removeAsync( AsyncCallback<Long> callback )
    {
        Backendless.Data.of( Backend.class ).remove( this, callback );
    }

    public static Backend findById( String id )
    {
        return Backendless.Data.of( Backend.class ).findById( id );
    }

    public static void findByIdAsync( String id, AsyncCallback<Backend> callback )
    {
        Backendless.Data.of( Backend.class ).findById( id, callback );
    }

    public static Backend findFirst()
    {
        return Backendless.Data.of( Backend.class ).findFirst();
    }

    public static void findFirstAsync( AsyncCallback<Backend> callback )
    {
        Backendless.Data.of( Backend.class ).findFirst( callback );
    }

    public static Backend findLast()
    {
        return Backendless.Data.of( Backend.class ).findLast();
    }

    public static void findLastAsync( AsyncCallback<Backend> callback )
    {
        Backendless.Data.of( Backend.class ).findLast( callback );
    }

    public static List<Backend> find( DataQueryBuilder queryBuilder )
    {
        return Backendless.Data.of( Backend.class ).find( queryBuilder );
    }

    public static void findAsync( DataQueryBuilder queryBuilder, AsyncCallback<List<Backend>> callback )
    {
        Backendless.Data.of( Backend.class ).find( queryBuilder, callback );
    }
}

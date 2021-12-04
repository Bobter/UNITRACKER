package com.example.familytracker

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

import java.time.LocalDateTime

val db: FirebaseFirestore = FirebaseFirestore.getInstance()

val TAG = "DEBUG"


class Position(longitude: Double, latitude: Double)
/*
 * Position of a user at certain time
 * NOTE: time is set to the creation instance
 */
{
    init {
        val time =  LocalDateTime.now() // ERROR: THIS FUNCTION REQUIRE: API level 26 (in use: 23)
        val longitude: Double = longitude
        val latitude: Double = latitude
    }
}

class User
{
    var name: String? = null
    var email: String? = null // change to a more suitable data type
    var positions: Map<String, Position>? = null
    var trackedGroups: Set<String>? = null // reference to tracked groups
    var belongGroups: Set<String>? = null // reference to groups that this user belong
    var document: DocumentReference? = null  //refence to user document

    constructor(userid: String)
    {
        User.collection.document(userid).get()
            .addOnSuccessListener { doc ->
                // Init group with data of firebase
                // SOME STUFF
                val x=0
            }
        this.document = User.collection.document(userid)
    }

    constructor(name: String, email: String)
    /*
     * Create a new user
     */
    {
        this.name = name
        this.email = email
        this.document = User.collection.document()
    }

    fun addPosition(position: Position)
    /*
     * Add a position of a user
     */
    {}

    companion object{
        @JvmField
        val collection: CollectionReference = db.collection("users")

        @JvmStatic fun add(user: User)
        /*
         * Add a user (document) to users firebase collection
         */
        {
            User.collection.add(user)
        }
        @JvmStatic fun remove(userid: String)
        /*
         * Remove a user (document) from users firebase collection
         */
        {
            User.collection.document(userid)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "User was successfully deleted") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }
}

class Group
/*
 * Group to tracking several users
 */
{
    var name: String? = null
    var admins: Set<String>? = null // reference to trackers
    var users: Set<String>? = null  // reference to tracked users
    var document: DocumentReference? = null

    constructor(groupId: String)
    {
        Group.collection.document(groupId).get()
            .addOnSuccessListener { doc ->
                // Init group with data of firebase
                // SOME STUFF
                val x=0
            }
        this.document = Group.collection.document(groupId)
    }
    constructor(name:String, admins: Set<String>, users: Set<String>)
    {
        this.name = name
        this.admins = admins
        this.users = users
        this.document = Group.collection.document()
    }

    fun addAdmin(userid: String)
    /*
     * Add an user to admins
     */
    {}

    fun removeAdmin(userid: String)
    /*
     * Remove the user with userid from the admins
     */
    {}

    fun addAUser(userid: String)
    /*
     * Add an user to users
     */
    {}

    fun removeUser(userid: String)
    /*
     * Remove the user with userid from the users
     */
    {}


    fun positions(): Map<User, Position>
    /*
     * Return the last position of all the member of this group
     */
    {
        val lastUserPositions: Map<User, Position> = HashMap<User, Position>()

        // MORE STUFF

        return lastUserPositions
    }

    companion object{
        @JvmField
        val collection: CollectionReference = db.collection("groups")

        @JvmStatic fun add(group: Group)
        /*
         * Add a group (document) to groups firebase collection
         */
        {
            Group.collection.add(group)
        }
        @JvmStatic fun remove(groupid: String)
        /*
         * Remove a group (document) from groups firebase collection
         */
        {
            Group.collection.document(groupid)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Group was successfully deleted") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }
}

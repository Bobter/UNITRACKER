package com.example.unitrackerv12

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue

import java.time.LocalDateTime

val db: FirebaseFirestore = FirebaseFirestore.getInstance()
var auth: FirebaseAuth = FirebaseAuth.getInstance()

val TAG = "DEBUG"


class Position(longitude: Double, latitude: Double)
/*
 * Position of a user at certain time
 * NOTE: time is set to the creation instance
 */
{
    init {
        val longitude: Double = longitude
        val latitude: Double = latitude
        val time =  LocalDateTime.now() // ERROR: THIS FUNCTION REQUIRE: API level 26 (in use: 23)
    }
}

class User
{
    var name: String? = null
    var email: String? = null // change to a more suitable data type
    lateinit var lastPosition: Position
    var positions: Map<String, Position>? = null
    var trackedGroups: Set<String>? = null // reference to tracked groups
    var belongGroups: Set<String>? = null // reference to groups that this user belong
    private lateinit var document: DocumentReference  //refence to user document

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

    fun addPosition(position: Position)
    /*
     * Add a position of a user
     */
    {
        this.lastPosition = position
        this.document.update("lastPosition", position)
        this.document.collection("positions").add(position)
            .addOnSuccessListener { Log.d(TAG, "New position successfully added!") }
            .addOnFailureListener {  e -> Log.w(TAG, "Error adding position document", e)}
    }

    companion object{
        @JvmField
        val collection: CollectionReference = db.collection("users")

        @JvmStatic fun createWithEmailAndPassword(email:String, password: String): User
        /*
         * Create an user with (email, password)
         */
        {
            auth.createUserWithEmailAndPassword(email, password)
            var user = auth.currentUser
            return User(user!!.uid)
        }
        @JvmStatic fun remove(userid: String)
        /*
         * Remove a user from users firebase collection
         */
        {
            User.collection.document(userid)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "User was successfully deleted") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
        @JvmStatic fun getCurrentUser(): User
        {
            val user: FirebaseUser? = auth.currentUser
            return User(user!!.uid)
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
            .addOnSuccessListener { document ->
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
            }
        this.document = Group.collection.document(groupId)
    }
    constructor(name:String, admins: Set<String>, users: Set<String>)
    {
        this.name = name
        this.admins = admins
        this.users = users

        var data = mapOf(
            name to this.name,
            admins to this.admins,
            users to this.users
        )

        Group.collection.add(data)
            .addOnSuccessListener { doc ->
                this.document = doc
            }
    }

    fun addAdmin(userid: String)
    /*
     * Add an user to admins
     */
    {
        this.document?.update("admins", FieldValue.arrayUnion(userid))
    }

    fun removeAdmin(userid: String)
    /*
     * Remove the user with userid from the admins
     */
    {
        this.document?.update("admins", FieldValue.arrayRemove(userid))
    }

    fun addAUser(userid: String)
    /*
     * Add an user to users
     */
    {
        this.document?.update("users", FieldValue.arrayUnion(userid))
    }

    fun removeUser(userid: String)
    /*
     * Remove the user with userid from the users
     */
    {
        this.document?.update("users", FieldValue.arrayRemove(userid))
    }


    fun lastPositions(): Map<User, Position>
    /*
     * Return the last position of all the member of this group
     */
    {
        val lastUserPositions: Map<User, Position> = HashMap<User, Position>()

        this.document?.get()
            ?.addOnSuccessListener { document ->
                //SOMETHING
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

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
